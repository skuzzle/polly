package polly.core.plugins;

import polly.core.ModuleStates;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = {
        @Require(component = PluginManagerImpl.class),
        @Require(state = ModuleStates.PLUGINS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.IRC_READY),
        @Require(state = ModuleStates.USERS_READY),
    },
    provides =
        @Provide(state = ModuleStates.PLUGINS_NOTIFIED)
    )
public class NotifyPluginsModule extends AbstractModule {

    private PluginManagerImpl pluginManager;



    public NotifyPluginsModule(ModuleLoader loader) {
        super("PLUGIN_NOTIFIER", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.pluginManager = this.requireNow(PluginManagerImpl.class);
    }



    @Override
    public void setup() throws SetupException {}



    @Override
    public void run() throws Exception {
        this.pluginManager.notifyPlugins();
        this.addState(ModuleStates.PLUGINS_NOTIFIED);
    }
}
