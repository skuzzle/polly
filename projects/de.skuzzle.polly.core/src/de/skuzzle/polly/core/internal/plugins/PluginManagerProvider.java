package de.skuzzle.polly.core.internal.plugins;

import java.io.IOException;

import de.skuzzle.polly.core.Polly;
import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.mypolly.MyPollyImpl;
import de.skuzzle.polly.core.util.ProxyClassLoader;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import polly.moduleloader.AbstractProvider;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;

@Module(
    requires = {
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = ProxyClassLoader.class)
    },
    provides = {
        @Provide(component = PluginManagerImpl.class),
        @Provide(state = ModuleStates.PLUGINS_READY)
    })
public class PluginManagerProvider extends AbstractProvider {
    
    public final static String PLUGIN_CFG = "plugin.cfg";
    
    private PluginManagerImpl pluginManager;
    private Configuration pluginCfg;
    private ShutdownManagerImpl shutdownManager;
    private ProxyClassLoader pollyCl;
    
    
    
    public PluginManagerProvider(ModuleLoader loader) {
        super("PLUGIN_MANAGER_PROVIDER", loader, false);
    }

    
    
    @Override
    public void beforeSetup() {
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class, true);
        this.pollyCl = this.requireNow(ProxyClassLoader.class, true);
    }
    
    
    
    @Override
    public void setup() throws SetupException {
        ConfigurationProvider configProvider = this.requireNow(
            ConfigurationProviderImpl.class, true);
        try {
            this.pluginCfg = configProvider.open(PLUGIN_CFG);
        } catch (IOException e) {
            throw new SetupException(e);
        }
        this.pluginManager = new PluginManagerImpl(this.pollyCl);
        
        this.provideComponent(this.pluginManager);
        this.shutdownManager.addDisposable(this.pluginManager);
    }

    

    public void run() throws Exception {
        MyPollyImpl myPolly = this.requireNow(MyPollyImpl.class, false);
        
        try {
            this.pluginManager.loadFolder(Polly.PLUGIN_FOLDER, myPolly, 
                this.pluginCfg.readStringList(Configuration.PLUGIN_EXCLUDES));
        } finally {
            this.addState(ModuleStates.PLUGINS_READY);
        }
    }

    
    
    @Override
    public void dispose() {
        this.pluginCfg = null;
        this.pluginManager = null;
        this.pollyCl = null;
        this.shutdownManager = null;
        super.dispose();
    }
}
