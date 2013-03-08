package de.skuzzle.polly.core.internal.plugins;

import de.skuzzle.polly.core.internal.ModuleStates;
import de.skuzzle.polly.core.internal.roles.RoleManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;

@Module(
    requires = {
        @Require(component = PluginManagerImpl.class),
        @Require(state = ModuleStates.PLUGINS_READY),
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.IRC_READY),
        @Require(state = ModuleStates.ROLES_READY),
        @Require(state = ModuleStates.USERS_READY)
    },
    provides =
        @Provide(state = ModuleStates.PLUGINS_NOTIFIED)
    )
public class NotifyPluginsProvider extends AbstractProvider {

    private PluginManagerImpl pluginManager;



    public NotifyPluginsProvider(ModuleLoader loader) {
        super("NOTIFY_PLUGINS_PROVIDER", loader, true);
    }



    @Override
    public void beforeSetup() {
        this.pluginManager = this.requireNow(PluginManagerImpl.class, true);
    }



    @Override
    public void setup() throws SetupException {}



    @Override
    public void run() throws Exception {
        
        // Get all contained permissions
        RoleManagerImpl roleManager = this.requireNow(RoleManagerImpl.class, false);
        
        for (Plugin plugin : this.pluginManager.loadedPlugins()) {
            roleManager.registerPermissions(plugin.getPluginInstance());
        }
        
        // assign permissions to roles
        for (Plugin plugin : this.pluginManager.loadedPlugins()) {
            try {
                plugin.getPluginInstance().assignPermissions(roleManager);
            } catch (Exception ignore) {
                logger.warn("Ignoring Exception: ", ignore);
            }
        }
        
        this.pluginManager.notifyPlugins();
        this.addState(ModuleStates.PLUGINS_NOTIFIED);
    }
    
    
    
    @Override
    public void dispose() {
        this.pluginManager = null;
        super.dispose();
    }
}
