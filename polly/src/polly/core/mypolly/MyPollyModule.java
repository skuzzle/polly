package polly.core.mypolly;

import polly.configuration.PollyConfiguration;
import polly.core.ConversationManagerImpl;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.formatting.FormatManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.core.plugins.PluginManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.util.AbstractPollyModule;
import polly.util.ModuleBlackboard;


public class MyPollyModule extends AbstractPollyModule {

    private CommandManagerImpl commandManager;
    private IrcManagerImpl ircManager;
    private PluginManagerImpl pluginManager;
    private PollyConfiguration config;
    private PersistenceManagerImpl persistencemanager;
    private UserManagerImpl userManager;
    private FormatManagerImpl formatManager;
    private ConversationManagerImpl conversationManager;
    private ShutdownManagerImpl shutdownManager;
    
    
    public MyPollyModule(ModuleBlackboard initializer) {
        super("MYPOLLY", initializer, true);
    }
    
    
    @Override
    public void require() {
        this.commandManager = this.requireComponent(CommandManagerImpl.class);
        this.ircManager = this.requireComponent(IrcManagerImpl.class);
        this.pluginManager = this.requireComponent(PluginManagerImpl.class);
        this.config = this.requireComponent(PollyConfiguration.class);
        this.persistencemanager = this.requireComponent(PersistenceManagerImpl.class);
        this.userManager = this.requireComponent(UserManagerImpl.class);
        this.formatManager = this.requireComponent(FormatManagerImpl.class);
        this.conversationManager = this.requireComponent(ConversationManagerImpl.class);
        this.shutdownManager = this.requireComponent(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public boolean doSetup() throws Exception {
        MyPollyImpl myPolly = new MyPollyImpl(
            this.commandManager, 
            this.ircManager, 
            this.pluginManager, 
            this.config, 
            this.persistencemanager, 
            this.userManager, 
            this.formatManager, 
            this.conversationManager,
            this.shutdownManager);
        this.provideComponent(MyPollyImpl.class, myPolly);
        return true;
    }
    
    
    
    @Override
    public void doRun() throws Exception {}

}
