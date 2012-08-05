package polly.core.mypolly;

import java.util.concurrent.ExecutorService;

import polly.configuration.ConfigurationProviderImpl;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.conversations.ConversationManagerImpl;
import polly.core.formatting.FormatManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.core.mail.MailManagerImpl;
import polly.core.paste.PasteServiceManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.core.plugins.PluginManagerImpl;
import polly.core.roles.RoleManagerImpl;
import polly.core.users.UserManagerImpl;
import polly.events.EventProvider;
import polly.moduleloader.AbstractModule;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = { 
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = IrcManagerImpl.class),
        @Require(component = PluginManagerImpl.class),
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = PersistenceManagerImpl.class),
        @Require(component = FormatManagerImpl.class),
        @Require(component = ConversationManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = UserManagerImpl.class),
        @Require(component = CommandManagerImpl.class),
        @Require(component = PasteServiceManagerImpl.class),
        @Require(component = ExecutorService.class),
        @Require(component = MailManagerImpl.class),
        @Require(component = RoleManagerImpl.class)
    },
    provides = 
        @Provide(component = MyPollyImpl.class))
public class MyPollyProvider extends AbstractModule {

    private CommandManagerImpl commandManager;
    private IrcManagerImpl ircManager;
    private PluginManagerImpl pluginManager;
    private ConfigurationProviderImpl config;
    private PersistenceManagerImpl persistencemanager;
    private UserManagerImpl userManager;
    private FormatManagerImpl formatManager;
    private ConversationManagerImpl conversationManager;
    private PasteServiceManagerImpl pasteManager;
    private ShutdownManagerImpl shutdownManager;
    private MailManagerImpl mailManager;
    private RoleManagerImpl roleManager;
    
    
    public MyPollyProvider(ModuleLoader loader) {
        super("MYPOLLY_PROVIDER", loader, true);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.commandManager = this.requireNow(CommandManagerImpl.class, true);
        this.ircManager = this.requireNow(IrcManagerImpl.class, true);
        this.pluginManager = this.requireNow(PluginManagerImpl.class, true);
        this.config = this.requireNow(ConfigurationProviderImpl.class, true);
        this.persistencemanager = this.requireNow(PersistenceManagerImpl.class, true);
        this.userManager = this.requireNow(UserManagerImpl.class, true);
        this.formatManager = this.requireNow(FormatManagerImpl.class, true);
        this.conversationManager = this.requireNow(ConversationManagerImpl.class, true);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class, true);
        this.pasteManager = this.requireNow(PasteServiceManagerImpl.class, true);
        this.mailManager = this.requireNow(MailManagerImpl.class, true);
        this.roleManager = this.requireNow(RoleManagerImpl.class, true);
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
            this.shutdownManager,
            this.pasteManager,
            this.mailManager,
            this.roleManager);
        this.provideComponent(myPolly);
    }
    
    
    
    @Override
    public void dispose() {
        this.commandManager = null;
        this.ircManager = null;
        this.pluginManager = null;
        this.config = null;
        this.persistencemanager = null;
        this.userManager = null;
        this.formatManager = null;
        this.conversationManager = null;
        this.shutdownManager = null;
        this.pasteManager = null;
        this.mailManager = null;
        super.dispose();
    }
}
