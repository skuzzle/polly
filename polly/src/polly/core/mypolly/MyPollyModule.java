package polly.core.mypolly;

import polly.configuration.PollyConfiguration;
import polly.core.AbstractModule;
import polly.core.ModuleLoader;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.conversations.ConversationManagerImpl;
import polly.core.formatting.FormatManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.core.plugins.PluginManagerImpl;
import polly.core.users.UserManagerImpl;


public class MyPollyModule extends AbstractModule {

    private CommandManagerImpl commandManager;
    private IrcManagerImpl ircManager;
    private PluginManagerImpl pluginManager;
    private PollyConfiguration config;
    private PersistenceManagerImpl persistencemanager;
    private UserManagerImpl userManager;
    private FormatManagerImpl formatManager;
    private ConversationManagerImpl conversationManager;
    private ShutdownManagerImpl shutdownManager;
    
    
    public MyPollyModule(ModuleLoader loader) {
        super("MODULE_MYPOLLY", loader, true);
        this.requireBeforeSetup(CommandManagerImpl.class);
        this.requireBeforeSetup(IrcManagerImpl.class);
        this.requireBeforeSetup(PluginManagerImpl.class);
        this.requireBeforeSetup(PollyConfiguration.class);
        this.requireBeforeSetup(PersistenceManagerImpl.class);
        this.requireBeforeSetup(UserManagerImpl.class);
        this.requireBeforeSetup(FormatManagerImpl.class);
        this.requireBeforeSetup(ConversationManagerImpl.class);
        this.requireBeforeSetup(ShutdownManagerImpl.class);
        
        this.willProvideDuringSetup(MyPollyImpl.class);
    }
    
    
    @Override
    public void beforeSetup() {
        this.commandManager = this.requireNow(CommandManagerImpl.class);
        this.ircManager = this.requireNow(IrcManagerImpl.class);
        this.pluginManager = this.requireNow(PluginManagerImpl.class);
        this.config = this.requireNow(PollyConfiguration.class);
        this.persistencemanager = this.requireNow(PersistenceManagerImpl.class);
        this.userManager = this.requireNow(UserManagerImpl.class);
        this.formatManager = this.requireNow(FormatManagerImpl.class);
        this.conversationManager = this.requireNow(ConversationManagerImpl.class);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class);
    }
    
    
    
    @Override
    public void setup() {
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
        this.provideComponent(myPolly);
    }
}
