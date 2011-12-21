package polly.core.plugins;

import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.ModuleStates;
import polly.core.SetupException;

public class NotifyPluginsModule extends AbstractModule {

    private PluginManagerImpl pluginManager;



    public NotifyPluginsModule(ModuleLoader loader) {
        super("PLUGIN_NOTIFIER", loader, true);

        this.requireBeforeSetup(PluginManagerImpl.class);

        this.requireState(ModuleStates.PLUGINS_READY);
        this.requireState(ModuleStates.PERSISTENCE_READY);
        this.requireState(ModuleStates.IRC_READY);
        this.requireState(ModuleStates.USERS_READY);

        this.willSetState(ModuleStates.PLUGINS_NOTIFIED);
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
