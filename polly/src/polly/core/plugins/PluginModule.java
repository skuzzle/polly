package polly.core.plugins;

import polly.configuration.PollyConfiguration;
import polly.core.ShutdownManagerImpl;
import polly.core.mypolly.MyPollyImpl;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class PluginModule extends AbstractPollyModule {
    
    private PluginManagerImpl pluginManager;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    private String pluginFolder;
    
    
    public PluginModule(ModuleBlackboard initializer, String pluginFolder) {
        super("PLUGINS", initializer, false);
        this.pluginFolder = pluginFolder;
    }

    
    
    @Override
    public void require() {
        this.config = this.requireComponent(PollyConfiguration.class);
        this.shutdownManager = this.requireComponent(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public boolean doSetup() throws Exception {
        this.pluginManager = new PluginManagerImpl();
        
        this.provideComponent(PluginManagerImpl.class, this.pluginManager);
        this.shutdownManager.addDisposable(this.pluginManager);
        return true;
    }

    
    
    @Override
    public void doRun() throws Exception {
        MyPollyImpl myPolly = this.requireComponent(MyPollyImpl.class);
        
        this.pluginManager.loadFolder(this.pluginFolder, myPolly, 
            this.config.getPluginExcludes());
    }

}
