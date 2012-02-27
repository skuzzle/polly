package polly.core.plugins;

import polly.configuration.PollyConfiguration;
import polly.core.ModuleStates;
import polly.core.ShutdownManagerImpl;
import polly.core.mypolly.MyPollyImpl;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = {
        @Require(component = PollyConfiguration.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = PollyClassLoader.class),
    },
    provides = {
        @Provide(component = PluginManagerImpl.class),
        @Provide(state = ModuleStates.PLUGINS_READY)
    })
public class PluginModule extends AbstractModule {
    
    private PluginManagerImpl pluginManager;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    private PollyClassLoader pollyCl;
    
    
    public PluginModule(ModuleLoader loader) {
        super("MODULE_PLUGINS", loader, false);
    }

    
    
    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
        this.pollyCl = this.requireNow(PollyClassLoader.class);
    }
    
    
    
    @Override
    public void setup() {
        this.pluginManager = new PluginManagerImpl(this.pollyCl);
        
        this.provideComponent(this.pluginManager);
        this.shutdownManager.addDisposable(this.pluginManager);
    }

    
    

    public void run() throws Exception {
        MyPollyImpl myPolly = this.requireNow(MyPollyImpl.class);
        
        try {
            this.pluginManager.loadFolder(this.config.getPluginFolder(), myPolly, 
                this.config.getPluginExcludes());
        } finally {
            this.addState(ModuleStates.PLUGINS_READY);
        }
    }

}
