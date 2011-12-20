package polly.core.plugins;

import polly.util.ModuleBlackboard;


public class NotifyPluginsAction extends ModuleBlackboard.Action {

    public NotifyPluginsAction(ModuleBlackboard blackboard) {
        super(blackboard);
    }
    
    

    @Override
    public void action() {
        PluginManagerImpl pluginManager = this.blackBoard.requireComponent(
                PluginManagerImpl.class);
        
        pluginManager.notifyPlugins();
    }
}
