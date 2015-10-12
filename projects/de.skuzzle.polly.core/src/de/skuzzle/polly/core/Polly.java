package de.skuzzle.polly.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.util.Arrays;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.skuzzle.polly.core.commandline.AbstractArgumentParser;
import de.skuzzle.polly.core.commandline.ParameterException;
import de.skuzzle.polly.core.commandline.PollyArgumentParser;
import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleBootstrapper;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.DefaultModuleLoader;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.util.ProxyClassLoader;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.Version;
import de.skuzzle.polly.sdk.resources.LocaleProvider;



public class Polly {

    public static Version getPollyVersion() {
        final String version = Polly.class.getPackage().getImplementationVersion();
        if (version == null) {
            return new Version(DEVELOP_VERSION);
        } else {
            return new Version(version);
        }
    }



    public static String getPid() {
        final String[] parts = ManagementFactory.getRuntimeMXBean().getName()
            .split("@"); //$NON-NLS-1$
        return parts[0];
    }



    /**
     * If polly transforms into SkyNet, the assumption is that this method returns true.
     * In order to save the world, any polly start up is prevented in this case.
     * @return Whether polly transformed into skynet.
     */
    private static boolean isSkynet() {
        return false;
    }



    public static File getPollyPath() {
        return new File("."); //$NON-NLS-1$
    }

    private static String commandLine = ""; //$NON-NLS-1$
    private static String[] commandLineArgs = {};



    public static String getCommandLine() {
        return commandLine;
    }



    public static String[] getCommandLineArgs() {
        return commandLineArgs;
    }



    public final static String DEVELOP_VERSION = "0.9.1 - dev"; //$NON-NLS-1$
    public final static String CONFIG_FULL_PATH = "cfg/polly.cfg"; //$NON-NLS-1$
    public final static String CONFIG_FILE_NAME = "polly.cfg"; //$NON-NLS-1$
    public final static String CONFIG_DIR = "./cfg"; //$NON-NLS-1$

    /**
     * The polly plugin folder. That folder is not supposed to change
     */
    public final static String PLUGIN_FOLDER = "plugins"; //$NON-NLS-1$

    private static Logger logger = Logger.getLogger(Polly.class.getName());



    /**
     * Start polly.
     *
     * @param args The commandline arguments passed to polly. This array may be empty.
     * @throws Exception If launching fails.
     */
    public synchronized static void main(String[] args) throws Exception {
        // First thing we do is to check for skynet
        if (isSkynet()) {
            System.out.println("Polly transformed into SkyNet. Saving the world by immediate shut down");
            return;
        }

        commandLineArgs = args;
        for (final String arg : args) {
            if (arg.indexOf(' ') != -1) {
                commandLine += "\"" + arg + "\" "; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                commandLine += arg + " "; //$NON-NLS-1$
            }
        }
        commandLine = commandLine.trim();

        final ProxyClassLoader parentCl = (ProxyClassLoader)
                    Thread.currentThread().getContextClassLoader();

        new Polly(args, parentCl);
    }



    public Polly(String[] args, ProxyClassLoader parentCl) {
        final ConfigurationProviderImpl configurationProvider =
            new ConfigurationProviderImpl(new File(CONFIG_DIR));

        Configuration pollyCfg = null;
        try {
            System.out.println("Loading main configuration file"); //$NON-NLS-1$
            pollyCfg = configurationProvider.open(CONFIG_FILE_NAME, true,
                    ConfigurationProviderImpl.NOP_VALIDATOR);

            System.out.println("Configuring Logger..."); //$NON-NLS-1$
            PropertyConfigurator.configure(
                pollyCfg.readString(Configuration.LOG_CONFIG_FILE));

            System.out.println("Logger configured. Switching to using logger"); //$NON-NLS-1$

            // initialize Locale as early as possible
            LocaleProvider.initLocale(pollyCfg);


            if (!lockInstance(new File("polly.lck"))) { //$NON-NLS-1$
                System.out.println(MSG.alreadyRunning);
                return;
            }


        } catch (final FileNotFoundException e) {
            System.out.println(MSG.bind(MSG.configError,
                    MSG.bind(MSG.configErrorReason1, CONFIG_FILE_NAME)));
            e.printStackTrace();
        } catch (final IOException e) {
            System.out.println(MSG.bind(MSG.configError,
                    MSG.bind(MSG.configErrorReason2, e.getMessage())));
            e.printStackTrace();
        } catch (final ConfigurationException e) {
            System.out.println(MSG.bind(MSG.configError,
                    MSG.bind(MSG.configErrorReason3, e.getMessage())));
            e.printStackTrace();
        }

        if (pollyCfg == null) {
            return;
        }

        logger.info(""); //$NON-NLS-1$
        logger.info(""); //$NON-NLS-1$
        logger.info(""); //$NON-NLS-1$
        parseArguments(args, pollyCfg);

        logger.info("----------------------------------------------"); //$NON-NLS-1$
        logger.info("new polly session started!"); //$NON-NLS-1$
        logger.info("----------------------------------------------"); //$NON-NLS-1$
        logger.info(""); //$NON-NLS-1$
        logger.info("Config file read from '" + CONFIG_FULL_PATH + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.info("Polly command line arguments: " + Arrays.toString(args)); //$NON-NLS-1$
        logger.info("(Canonical: " + getCommandLine() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        final Version version = getPollyVersion();
        logger.info("Version info: " + version); //$NON-NLS-1$
        logger.info("Java Version: " + System.getProperty("java.version")); //$NON-NLS-1$ //$NON-NLS-2$
        logger.info("Java Home: " + System.getProperty("java.home")); //$NON-NLS-1$ //$NON-NLS-2$
        logger.info("Classpath: \n    " + System.getProperty("java.class.path").replace( //$NON-NLS-1$ //$NON-NLS-2$
            File.pathSeparator, "\n    ")); //$NON-NLS-1$


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
        final ModuleLoader loader = new DefaultModuleLoader();

        // Modules:
        // Register all the modules. The order in which they are registered does not
        // matter as the setup order will be automatically determined by the
        // ModuleLoader. Just make sure your module properly reports all its
        // dependencies!


        try {
            // This parses the modules.cfg and instantiates all listed modules
            final File modulesCfg = new File(pollyCfg.readString(Configuration.MODULE_CONFIG));
            ModuleBootstrapper.prepareModuleLoader(loader, modulesCfg);

            // add startup module
            new AnonymousModule(loader, configurationProvider, parentCl);

            logger.info("Running setup for all modules..."); //$NON-NLS-1$
            loader.runSetup();
            logger.info("Setup done. Now executing each module..."); //$NON-NLS-1$
            loader.runModules();

            if (pollyCfg.readBoolean(Configuration.DEBUG_MODE)) {
                loader.exportToDot(new File("modules.dot")); //$NON-NLS-1$
            }
        } catch (final Exception e) {
            logger.fatal("Crucial component failed to load!", e); //$NON-NLS-1$

            final ShutdownManagerImpl s = loader.requireNow(ShutdownManagerImpl.class);
            s.shutdown(true);
        } finally {
            loader.dispose();
        }

        logger.info("Polly succesfully set up."); //$NON-NLS-1$
    }



    @Module(startUp = true, provides = {
        @Provide(component = ShutdownManagerImpl.class),
        @Provide(component = ConfigurationProviderImpl.class),
        @Provide(component = ProxyClassLoader.class),
    })
    private static class AnonymousModule extends AbstractProvider {

        private final ConfigurationProviderImpl configurationProvider;
        private final ProxyClassLoader parentCl;


        public AnonymousModule(ModuleLoader loader,
                ConfigurationProviderImpl configurationProvider,
                ProxyClassLoader parentCl) {
            super("ROOT_PROVIDER", loader, true); //$NON-NLS-1$
            this.parentCl = parentCl;
            this.configurationProvider = configurationProvider;
        }



        @Override
        public void setup() throws SetupException {
            provideComponent(this.configurationProvider);
            provideComponent(this.parentCl);
            provideComponent(new ShutdownManagerImpl());

            if (this.configurationProvider.getRootConfiguration().readBoolean(
                Configuration.DEBUG_MODE)) {
                this.logger.warn(
                    "\n\nPOLLY DEBUG MODE IS ENABLED. DISABLE THIS IN PRODUCTIVE SYSTEMS!\n\n"); //$NON-NLS-1$
            }
        }

    }



    private boolean lockInstance(final File file) {
        try {
            final boolean existed = file.exists();
            if (!existed) {
                file.createNewFile();
            }
            final RandomAccessFile randomAccessFile = new RandomAccessFile(
                file, "rw"); //$NON-NLS-1$
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {

                    @Override
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            if (!existed) {
                                file.delete();
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private void parseArguments(String[] args, Configuration config) {
        try {
            final AbstractArgumentParser parser = new PollyArgumentParser(config);
            parser.parse(args);
            commandLine = parser.getCanonicalArguments();
        } catch (final ParameterException e) {
            logger.warn(e.getMessage(), e);
            printHelp();
            System.exit(0);
        }
    }



    private void printHelp() {
        System.out.println(MSG.paramCaption);
        System.out.println(MSG.paramHeader);
        System.out.println(MSG.paramLog);
        System.out.println(MSG.paramNick);
        System.out.println(MSG.paramIdent);
        System.out.println(MSG.paramServer);
        System.out.println(MSG.paramPort);
        System.out.println(MSG.paramJoin);
        System.out.println(MSG.paramHelp);
        System.out.println(MSG.paramClose);
        try {
            System.in.read();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
