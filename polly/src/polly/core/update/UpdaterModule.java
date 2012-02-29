package polly.core.update;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import de.skuzzle.polly.process.JavaProcessExecutor;
import de.skuzzle.polly.process.ProcessExecutor;
import de.skuzzle.polly.sdk.Version;
import polly.Polly;
import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.plugins.Plugin;
import polly.core.plugins.PluginManagerImpl;
import polly.util.FileUtil;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;



@Module(
    requires = {
        @Require(component = PollyConfiguration.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = PluginManagerImpl.class),
    })
public class UpdaterModule extends AbstractModule {

    private PollyConfiguration config;
    private PluginManagerImpl pluginManager;
    private ShutdownManagerImpl shutdownManager;



    public UpdaterModule(ModuleLoader loader) {
        super("MODULE_UPDATER", loader, false);
    }



    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.pluginManager = this.requireNow(PluginManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);

    }



    @Override
    public void setup() throws SetupException {
        this.updateInstaller();
        this.checkUpdates();
    }



    private void updateInstaller() {
        if (!this.config.getAutoUpdate()) {
            return;
        }

        UpdateItem ui = null;
        try {
            String version = "0.0.0";
            if ((new File("polly.installer.jar")).exists()) {
                JarFile installer = new JarFile("polly.installer.jar");
                Manifest m = installer.getManifest();
                Attributes main = m.getMainAttributes();
                version = main.getValue("Implementation-Version");
            }
            ui = new UpdateItem("polly.installer", new Version(version),
                new URL(this.config.getInstallerUpdateUrl()));
        } catch (IOException e) {
            logger.error("Error while updating installer", e);
        }

        UpdateManager um = new UpdateManager();
        logger.info("checking for new installer version...");
        List<UpdateProperties> update = um.collect(Collections
            .singletonList(ui));
        if (update.isEmpty()) {
            return;
        }

        logger.info("Downloading installer update...");
        List<File> files = um.downloadUpdates(update);
        if (files.size() != 1) {
            return;
        }
        logger.info("installing installer update...");
        try {
            File zip = files.get(0);
            File temp = FileUtil.createTempDirectory();
            FileUtil.unzip(zip, temp);
            FileUtil.copyContent(temp, Polly.getPollyPath());
            FileUtil.deleteRecursive(temp);
            zip.delete();
            logger.info("installer updated successfuly");
        } catch (IOException e) {
            logger.error("Error while updating installer.jar", e);
            return;
        }
    }



    private void checkUpdates() {
        if (!this.config.getAutoUpdate()) {
            return;
        } else if (!(new File("polly.installer.jar")).exists()) {
            logger.error("'installer.jar' not found in polly root directory. "
                + "Skipping updates");
            return;
        }
        List<Plugin> plugins = this.pluginManager.enumerate(
            Polly.PLUGIN_FOLDER, this.config.getPluginExcludes());

        List<UpdateItem> updates = new LinkedList<UpdateItem>();

        try {
            updates.add(new UpdateItem("polly", Polly.getPollyVersion(),
                new URL(config.getUpdateUrl())));
        } catch (MalformedURLException e) {
            // please never reach
            logger
                .fatal(
                    "Unable to add update item for polly: "
                        + config.getUpdateUrl(), e);
        }

        for (Plugin pc : plugins) {
            if (!pc.updateSupported()) {
                continue;
            }
            try {
                updates.add(UpdateItem.fromProperties(pc.getProps()));
            } catch (Exception e) {
                logger.error(
                    "Failed to create update item for plugin "
                        + pc.getProperty(Plugin.PLUGIN_NAME), e);
            }
        }
        UpdateManager um = new UpdateManager();

        logger.debug("Collecting updates...");
        List<UpdateProperties> actualUpdates = um.collect(updates);
        if (actualUpdates.isEmpty()) {
            logger.info("No updates available.");
            return;
        }
        logger.debug("Downloading updates...");
        List<File> files = um.downloadUpdates(actualUpdates);
        if (files.isEmpty()) {
            logger.info("No downloads available. Skipping update");
            return;
        }

        // create setup.dat for all updates that should have been downloaded.
        // um.createSetupFile(actualUpdates);

        logger.debug("Preparing to install downloaded updates.");
        final ProcessExecutor pe = JavaProcessExecutor.getOsInstance(false); // do
                                                                             // not
                                                                             // run
                                                                             // installer
                                                                             // in
                                                                             // console
        pe.addCommandsFromString("-jar polly.installer.jar");

        if (!Polly.getCommandLine().equals("")) {
            pe.addCommand("-pp");
            pe.addCommand(Polly.getCommandLine());
        }

        pe.addCommand("-f");
        StringBuilder b = new StringBuilder();
        for (File file : files) {
            b.append(file.getAbsolutePath());
            b.append(";");
        }
        pe.addCommand(b.toString());

        try {
            logger.info("Launching installer...");
            pe.start();
            logger.trace("Command: " + pe.toString());
            this.shutdownManager.shutdown(true);
        } catch (IOException e) {
            logger.fatal(
                "Failed to start the installer. Deleting all downloads", e);
            for (File file : files) {
                file.delete();
            }
        }
    }
    
    
    
    @Override
    public void dispose() {
        this.config = null;
        this.pluginManager = null;
        this.shutdownManager = null;
        super.dispose();
    }
}
