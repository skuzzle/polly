package de.skuzzle.polly.core.internal.persistence;


import java.io.IOException;

import de.skuzzle.polly.core.Polly;
import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.plugins.PluginManagerImpl;
import de.skuzzle.polly.core.internal.roles.Permission;
import de.skuzzle.polly.core.internal.roles.Role;
import de.skuzzle.polly.core.internal.users.UserImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;

@Module(
    requires = {
        @Require(component = PluginManagerImpl.class),
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(state = ModuleStates.PLUGINS_READY)
    },
    provides = {
        @Provide(component = PersistenceManagerV2Impl.class),
        @Provide(state = ModuleStates.PERSISTENCE_READY)
    })
public class PersistenceManagerProvider extends AbstractProvider {

    public final static String PERSISTENCE_CONFIG = "persistence.cfg";
    
    
    private PluginManagerImpl pluginManager;
    private PersistenceManagerV2Impl persistenceManager;
    private ShutdownManagerImpl shutdownManager;
    private XmlCreator xmlCreator;
    private Configuration persistenceCfg;
    
    
    
    public PersistenceManagerProvider(ModuleLoader loader) {
        super("PERSISTENCE_MANAGER_PROVIDER", loader, true);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.pluginManager = this.requireNow(PluginManagerImpl.class, true);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class, true);
    }
    

    
    @Override
    public void setup() throws SetupException {
        ConfigurationProviderImpl configProvider = this.requireNow(
                ConfigurationProviderImpl.class, true);
        try {
            this.persistenceCfg = configProvider.open(PERSISTENCE_CONFIG);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        this.persistenceManager = new PersistenceManagerV2Impl();
        this.provideComponent(this.persistenceManager);
        
        DatabaseProperties dp = new DatabaseProperties(
            this.persistenceCfg.readString(Configuration.DB_PASSWORD),
            this.persistenceCfg.readString(Configuration.DB_USER),
            this.persistenceCfg.readString(Configuration.DB_DRIVER),
            this.persistenceCfg.readString(Configuration.DB_URL));
    
        this.xmlCreator = new XmlCreator(
                this.persistenceManager.getEntities(), 
                dp, 
                this.persistenceCfg.readString(Configuration.DB_PERSISTENCE_UNIT), 
                this.pluginManager,
                Polly.PLUGIN_FOLDER);
        
        this.persistenceManager.registerEntity(UserImpl.class);
        this.persistenceManager.registerEntity(Permission.class);
        this.persistenceManager.registerEntity(Role.class);
               
        this.shutdownManager.addDisposable(this.persistenceManager);
    }

    
    

    public void run() throws Exception {
        String persistenceXml = persistenceCfg.readString(
                Configuration.DB_PERSISTENCE_XML_PATH);
        logger.debug("Writing persistence.xml to " + persistenceXml);
        this.xmlCreator.writePersistenceXml(persistenceXml);
        
        this.persistenceManager.connect(
            this.persistenceCfg.readString(Configuration.DB_PERSISTENCE_UNIT));
        
        try {
            this.persistenceManager.runAllEntityConverters();
        } catch (DatabaseException e) {
            logger.error("Error while running entity converters", e);
        }
        
        this.addState(ModuleStates.PERSISTENCE_READY);
    }



    @Override
    public void dispose() {
        this.pluginManager = null;
        this.shutdownManager = null;
        this.persistenceManager = null;
        this.xmlCreator = null;
        super.dispose();
    }
}
