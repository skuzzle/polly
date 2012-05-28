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
import polly.core.ModuleBootstrapper;
import polly.core.ShutdownManagerImpl;
import polly.moduleloader.DefaultModuleLoader;
import polly.moduleloader.ModuleLoader;
import polly.util.FileUtil;
import polly.util.ProxyClassLoader;
import de.skuzzle.polly.config.ConfigurationFile;
import de.skuzzle.polly.config.ParseException;
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
    private static String[] commandLineArgs = {};



    public static String getCommandLine() {
        return commandLine;
    }
    
    
    
    public static String[] getCommandLineArgs() {
        return commandLineArgs;
    }
    
    

    private final static String DEVELOP_VERSION = "0.9.1 - dev";
    private final static String CONFIG_FULL_PATH = "cfg/polly.cfg";
    
    /**
     * The polly plugin folder. That folder is not supposed to change
     */
    public final static String PLUGIN_FOLDER = "plugins";

    private static Logger logger = Logger.getLogger(Polly.class.getName());



    /**
     * Start polly.
     * 
     * @param args
     *            The commandline arguments passed to polly. This array may be
     *            empty
     */
    public synchronized static void main(String[] args) throws Exception {
        commandLineArgs = args;
        for (String arg : args) {
            if (arg.indexOf(' ') != -1) {
                commandLine += "\"" + arg + "\" ";
            } else {
                commandLine += arg + " ";
            }
        }
        commandLine = commandLine.trim();

        ProxyClassLoader parentCl = (ProxyClassLoader) 
                    Thread.currentThread().getContextClassLoader();
        
        new Polly(args, parentCl);
    }



    public Polly(String[] args, ProxyClassLoader parentCl) {
        if (!this.lockInstance(new File("polly.lck"))) {
            System.out.println("Polly already running");
            return;
        }
        if (!FileUtil.waitFor("polly.installer.jar")) {
            System.out.println("Updates still running. Exiting!");
            return;
        }
        
        PollyConfiguration config = this.readConfig(CONFIG_FULL_PATH);
        ConfigurationFile newConfig = this.getConfig("./cfg/newConfig.cfg");
        System.out.println(newConfig.format(true, true));
        this.checkDirectoryStructure(config);

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
         * 
         * The modules to be loaded must be specified in the modules.cfg as pointed to
         * by Configuration#MODULES_CONFIG.
         */

        // Setup Module loader
        ModuleLoader loader = new DefaultModuleLoader();

        // Base components which have no own module:
        loader.provideComponent(new ShutdownManagerImpl());
        loader.provideComponent(config);
        loader.provideComponent(newConfig);
        loader.provideComponent(parentCl);

        // Modules:
        // Register all the modules. The order in which they are registered does not
        // matter as the setup order will be automatically determined by the 
        // ModuleLoader. Just make sure your module properly reports all its 
        // dependencies!
        
        
        try {
            // This parses the modules.cfg and instantiates all listed modules
            ModuleBootstrapper.prepareModuleLoader(loader, config);
            
            loader.runSetup();
            loader.runModules();
        } catch (Exception e) {
            logger.fatal("Crucial component failed to load!", e);
            
            ShutdownManagerImpl s = loader.requireNow(ShutdownManagerImpl.class);
            s.shutdown(true);
        } finally {
            loader.dispose();
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



    private void checkDirectoryStructure(PollyConfiguration config) {
        boolean success = true;
        File cfg_plugins = new File(Polly.PLUGIN_FOLDER);
        File cfg_metainf = new File(config.getPersistenceXML()).getParentFile();

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
    
    

    private ConfigurationFile getConfig(String path) {
        try {
            File configFile = new File(path);
            
            if (!configFile.exists()) {
                
            }
            
            return ConfigurationFile.open(configFile);
        } catch (IOException e) {
            System.out.println(
                "Encountered IO Exception while reading Configuration from " + path);
            e.printStackTrace();
            System.exit(-1);
        } catch (ParseException e) {
            System.out.println("Invalid config file: " + path);
            System.out.println("Encountered ParseException at " + e.getPosition() + ":");
            System.out.println(e.getMessage());
            System.out.println("StackTrace:");
            e.printStackTrace();
            System.exit(-1);
        }
        
        return null;
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
        System.out.println("Polly Parameterübersicht:");
        System.out.println("Name: \t\tParameter:\t Beschreibung:");
        System.out.println("(-log | -l) \t<'on'|'off'> \t Schaltet IRC "
            + "log ein/aus.");
        System.out
            .println("(-nick | -n) \t<nickname> \t Nickname für den Bot.");
        System.out
            .println("(-ident | -i) \t<ident> \t Passwort für den Nickserv.");
        System.out
            .println("(-server | -s) \t<server> \t Server zu dem sich der Bot "
                + "verbindet. Nutzt 6669 als Port wenn nicht anders angegeben.");
        System.out
            .println("(-port | -p) \t<port> \t\t Port für den IRC-Server.");
        System.out.println("(-join | -j) \t<#c1,..#cn> \t Joined nur die/den "
            + "angegebenen Channel.");
        System.out
            .println("(-update | -u) \t<'on'|'off'> \t Auto update ein/aus.");
        System.out
            .println("-telnet \t<'on'|'off'>\t Telnet-Server aktivieren.");
        System.out.println("-telnetport \t<port>\t\t Telnet-Port setzen.");
        System.out.println("(-help | -?) \t\t\t Diese Übersicht anzeigen.");
        System.out.println("Beliebige Taste drücken zum Beenden.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
