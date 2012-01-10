package polly.core.plugins;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.ModuleStates;
import polly.core.ShutdownManagerImpl;
import polly.core.annotation.Module;
import polly.core.annotation.Provide;
import polly.core.annotation.Require;
import polly.core.mypolly.MyPollyImpl;


@Module(
    requires = {
        @Require(component = PollyConfiguration.class),
        @Require(component = ShutdownManagerImpl.class),
    },
    provides = {
        @Provide(component = PluginManagerImpl.class),
        @Provide(state = ModuleStates.PLUGINS_READY)
    })
public class PluginModule extends AbstractModule {
    
    private PluginManagerImpl pluginManager;
    private PollyConfiguration config;
    private ShutdownManagerImpl shutdownManager;
    private String pluginFolder;
    
    
    public PluginModule(ModuleLoader loader, String pluginFolder) {
        super("MODULE_PLUGINS", loader, false);
        this.pluginFolder = pluginFolder;
    }

    
    
    @Override
    public void beforeSetup() {
        this.config = this.requireNow(PollyConfiguration.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public void setup() {
        this.pluginManager = new PluginManagerImpl();
        
        this.provideComponent(this.pluginManager);
        this.shutdownManager.addDisposable(this.pluginManager);
    }

    
    

    public void run() throws Exception {
        MyPollyImpl myPolly = this.requireNow(MyPollyImpl.class);
        
        this.pluginManager.loadFolder(this.pluginFolder, myPolly, 
            this.config.getPluginExcludes());
        
        this.addState(ModuleStates.PLUGINS_READY);
    }

}
