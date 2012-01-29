package polly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import polly.commandline.AbstractArgumentParser;
import polly.commandline.ParameterException;
import polly.commandline.PollyArgumentParser;
import polly.configuration.DefaultPollyConfiguration;
import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandModule;
import polly.core.conversations.ConversationModule;
import polly.core.executors.ExecutorModule;
import polly.core.formatting.FormatterModule;
import polly.core.irc.IrcModule;
import polly.core.mypolly.MyPollyModule;
import polly.core.paste.PasteServiceModule;
import polly.core.persistence.PersistenceModule;
import polly.core.plugins.NotifyPluginsModule;
import polly.core.plugins.PluginModule;
import polly.core.update.UpdaterModule;
import polly.core.users.UserModule;
import polly.moduleloader.DefaultModuleLoader;
import polly.moduleloader.ModuleLoader;
import polly.util.FileUtil;
import de.skuzzle.polly.sdk.Version;

public class Polly {

    public static Version getPollyVersion() {
        String version = Polly.class.getPackage().getImplementationVersion();
        if (version == null) {
            return new Version(DEVELOP_VERSION);
        } else {
            return new Version(version);
        }
    }



    public static String getPid() {
        String[] parts = ManagementFactory.getRuntimeMXBean().getName()
            .split("@");
        return parts[0];
    }



    public static File getPollyPath() {
        return new File(".");
    }

    private static String commandLine = "";



    public static String getCommandLine() {
        return commandLine;
    }

    private final static String DEVELOP_VERSION = "0.6.3";
    private final static String PLUGIN_FOLDER = "cfg/plugins/";
    private final static String CONFIG_FULL_PATH = "cfg/polly.cfg";
    private final static String PERSISTENCE_XML = "cfg/META-INF/persistence.xml";
    private final static String PERSISTENCE_UNIT = "polly";

    private static Logger logger = Logger.getLogger(Polly.class.getName());



    /**
     * Start polly.
     * 
     * @param args
     *            The commandline arguments passed to polly. This array may be
     *            empty
     */
    public synchronized static void main(String[] args) {
        for (String arg : args) {
            commandLine += arg + " ";
        }
        commandLine = commandLine.trim();
        new Polly(args);
    }



    private Polly(String[] args) {
        if (!this.lockInstance(new File("polly.lck"))) {
            System.out.println("Polly already running");
            return;
        }
        if (!FileUtil.waitFor("polly.installer.jar")) {
            System.out.println("Updates still running. Exiting!");
            return;
        }

        this.checkDirectoryStructure();

        PollyConfiguration config = this.readConfig(CONFIG_FULL_PATH);

        logger.info("");
        logger.info("");
        logger.info("");
        this.parseArguments(args, config);

        logger.info("----------------------------------------------");
        logger.info("new polly session started!");
        logger.info("----------------------------------------------");
        logger.info("");
        logger.info("Config file read from '" + CONFIG_FULL_PATH + "'");
        logger.info("Polly command line arguments: " + Arrays.toString(args));
        logger.info("(Canonical: " + getCommandLine() + ")");
        Version version = getPollyVersion();
        logger.info("Version info: " + version);
        logger.info("Java Version: " + System.getProperty("java.version"));
        logger.info("Java Home: " + System.getProperty("java.home"));

        logger.trace("Configuration: \n" + config.toString());

        if (config.getPluginExcludes().length != 0) {
            logger.info("Following plugins are excluded: "
                + Arrays.toString(config.getPluginExcludes()));
        }
        if (config.getIgnoredCommands().length != 0) {
            logger.info("Following commands will be ignored: "
                + Arrays.toString(config.getIgnoredCommands()));
        }

        /*
         * POLLY INITIALIZATION:
         * 
         * Each component to be set up has its own Module subclass which does
         * all the set up things for this component. While setting up, this
         * module can provide all components that it has initialized to the
         * other modules that will be setup next. This process is executed by
         * the ModuleBlackboard.
         * 
         * Each module must report its dependencies and provided components in the
         * constructor. Given that information for every single module, the module
         * loader determines the order in which the modules are loaded so all 
         * dependencies can be satisfied.
         */

        // Setup Module loader
        ModuleLoader loader = new DefaultModuleLoader();

        // Base components which have no own module:
        loader.provideComponent(new ShutdownManagerImpl());
        loader.provideComponent(config);

        // Modules:
        // Register all the modules. The order in which they are registered does not
        // matter as the setup order will be automatically determined by the 
        // ModuleLoader. Just make sure your module properly reports all its 
        // dependencies!

        new ConversationModule(loader);
        new ExecutorModule(loader);
        new PluginModule(loader, PLUGIN_FOLDER);
        new UpdaterModule(loader, PLUGIN_FOLDER);
        new FormatterModule(loader);
        new PersistenceModule(loader, PERSISTENCE_XML, PERSISTENCE_UNIT);
        new CommandModule(loader);
        new UserModule(loader);
        new IrcModule(loader);
        new MyPollyModule(loader);
        new NotifyPluginsModule(loader);
        new PasteServiceModule(loader);
        
        try {
            loader.runSetup();
            loader.runModules();
        } catch (Exception e) {
            logger.fatal("Crucial component failed to load!", e);
            
            ShutdownManagerImpl s = loader.requireNow(ShutdownManagerImpl.class);
            s.shutdown(true);
        }

        logger.info("Polly succesfully set up.");
    }



    private boolean lockInstance(final File file) {
        try {
            final boolean existed = file.exists();
            if (!existed) {
                file.createNewFile();
            }
            final RandomAccessFile randomAccessFile = new RandomAccessFile(
                file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {

                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            if (!existed) {
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private void checkDirectoryStructure() {
        boolean success = true;
        File cfg_plugins = new File(PLUGIN_FOLDER);
        File cfg_metainf = new File(Polly.PERSISTENCE_XML).getParentFile();

        if (!cfg_plugins.exists()) {
            success &= cfg_plugins.mkdirs();
            System.out.println("creating " + cfg_plugins + ": " + success);
        }
        if (!cfg_metainf.exists()) {
            success &= cfg_metainf.mkdirs();
            System.out.println("creating " + cfg_metainf + ": " + success);
        }

        if (!success) {
            System.exit(0);
        }
    }



    private PollyConfiguration readConfig(String path) {
        try {
            File configFile = new File(path);

            /*
             * Create a default configuration file
             */
            if (!configFile.exists()) {
                DefaultPollyConfiguration defCfg = new DefaultPollyConfiguration();
                defCfg.store(new FileOutputStream(configFile), "");
            }

            PollyConfiguration config = new PollyConfiguration(path);
            PropertyConfigurator.configure(config.getLogConfigFile());
            return config;
        } catch (Exception e) {
            // Reply using stdout because logger may not be initialized
            System.out.println("Fehler beim Lesen der Konfigurationsdatei: "
                + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        // unreachable
        return null;
    }



    private void parseArguments(String[] args, PollyConfiguration config) {
        try {
            AbstractArgumentParser parser = new PollyArgumentParser(config);
            parser.parse(args);
            commandLine = parser.getCanonicalArguments();
        } catch (ParameterException e) {
            logger.warn(e.getMessage(), e);
            this.printHelp();
            System.exit(0);
        }
    }



    private void printHelp() {
        System.out.println("Polly Parameter�bersicht:");
        System.out.println("Name: \t\tParameter:\t Beschreibung:");
        System.out.println("(-log | -l) \t<'on'|'off'> \t Schaltet IRC "
            + "log ein/aus.");
        System.out
            .println("(-nick | -n) \t<nickname> \t Nickname f�r den Bot.");
        System.out
            .println("(-ident | -i) \t<ident> \t Passwort f�r den Nickserv.");
        System.out
            .println("(-server | -s) \t<server> \t Server zu dem sich der Bot "
                + "verbindet. Nutzt 6669 als Port wenn nicht anders angegeben.");
        System.out
            .println("(-port | -p) \t<port> \t\t Port f�r den IRC-Server.");
        System.out.println("(-join | -j) \t<#c1,..#cn> \t Joined nur die/den "
            + "angegebenen Channel.");
        System.out
            .println("(-update | -u) \t<'on'|'off'> \t Auto update ein/aus.");
        System.out
            .println("-telnet \t<'on'|'off'>\t Telnet-Server aktivieren.");
        System.out.println("-telnetport \t<port>\t\t Telnet-Port setzen.");
        System.out.println("(-help | -?) \t\t\t Diese �bersicht anzeigen.");
        System.out.println("Beliebige Taste dr�cken zum Beenden.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
