package polly.core.persistence;

import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.plugins.PluginManagerImpl;
import polly.data.Attribute;
import polly.data.User;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class PersistenceModule extends AbstractPollyModule {

    private String persistenceXMLPath;
    private String persistenceUnitName;
    private PollyConfiguration config;
    private PluginManagerImpl pluginManager;
    private PersistenceManagerImpl persistenceManager;
    private ShutdownManagerImpl shutdownManager;
    private XmlCreator xmlCreator;
    
    
    public PersistenceModule(ModuleBlackboard initializer, 
                String persistenceXMLPath, String persistenceUnitName) {
        super("PERSISTENCE", initializer, true);
        this.persistenceXMLPath = persistenceXMLPath;
        this.persistenceUnitName = persistenceUnitName;

    }
    
    
    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
        this.pluginManager = this.requireComponent(PluginManagerImpl.class);
        this.shutdownManager = this.requireComponent(ShutdownManagerImpl.class);
    }
    

    
    @Override
    public boolean doSetup() throws Exception {
        this.persistenceManager = new PersistenceManagerImpl();
        this.provideComponent(PersistenceManagerImpl.class, this.persistenceManager);
        
        DatabaseProperties dp = new DatabaseProperties(
            this.config.getDbPassword(), 
            this.config.getDbUser(), 
            this.config.getDbDriver(), 
            this.config.getDbUrl());
    
        this.xmlCreator = new XmlCreator(
                this.persistenceManager.getEntities(), 
                dp, 
                this.persistenceUnitName, 
                this.pluginManager);
        
        this.persistenceManager.registerEntity(User.class);
        this.persistenceManager.registerEntity(Attribute.class);
               
        this.shutdownManager.addDisposable(this.persistenceManager);
        return true;
    }

    
    
    @Override
    public void doRun() throws Exception {
        logger.debug("Writing persistence.xml to " + this.persistenceXMLPath);
        this.xmlCreator.writePersistenceXml(this.persistenceXMLPath);
        
        this.persistenceManager.connect(this.persistenceUnitName);
    }



}
