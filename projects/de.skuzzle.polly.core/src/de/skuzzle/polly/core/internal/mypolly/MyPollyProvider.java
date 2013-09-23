package de.skuzzle.polly.core.internal.mypolly;

import java.util.concurrent.ExecutorService;

import de.skuzzle.polly.core.configuration.ConfigurationProviderImpl;
import de.skuzzle.polly.core.internal.ShutdownManagerImpl;
import de.skuzzle.polly.core.internal.commands.CommandManagerImpl;
import de.skuzzle.polly.core.internal.conversations.ConversationManagerImpl;
import de.skuzzle.polly.core.internal.formatting.FormatManagerImpl;
import de.skuzzle.polly.core.internal.http.HttpManagerImpl;
import de.skuzzle.polly.core.internal.httpv2.WebInterfaceManagerImpl;
import de.skuzzle.polly.core.internal.irc.IrcManagerImpl;
import de.skuzzle.polly.core.internal.mail.MailManagerImpl;
import de.skuzzle.polly.core.internal.paste.PasteServiceManagerImpl;
import de.skuzzle.polly.core.internal.persistence.PersistenceManagerV2Impl;
import de.skuzzle.polly.core.internal.plugins.PluginManagerImpl;
import de.skuzzle.polly.core.internal.roles.RoleManagerImpl;
import de.skuzzle.polly.core.internal.runonce.RunOnceManagerImpl;
import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Provide;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.tools.events.EventProvider;


@Module(
    requires = { 
        @Require(component = ShutdownManagerImpl.class),
        @Require(component = IrcManagerImpl.class),
        @Require(component = PluginManagerImpl.class),
        @Require(component = ConfigurationProviderImpl.class),
        @Require(component = PersistenceManagerV2Impl.class),
        @Require(component = FormatManagerImpl.class),
        @Require(component = ConversationManagerImpl.class),
        @Require(component = EventProvider.class),
        @Require(component = UserManagerImpl.class),
        @Require(component = CommandManagerImpl.class),
        @Require(component = PasteServiceManagerImpl.class),
        @Require(component = ExecutorService.class),
        @Require(component = MailManagerImpl.class),
        @Require(component = RoleManagerImpl.class),
        @Require(component = RunOnceManagerImpl.class),
        @Require(component = HttpManagerImpl.class),
        @Require(component = WebInterfaceManagerImpl.class),
    },
    provides = 
        @Provide(component = MyPollyImpl.class))
public class MyPollyProvider extends AbstractProvider {

    private CommandManagerImpl commandManager;
    private IrcManagerImpl ircManager;
    private PluginManagerImpl pluginManager;
    private ConfigurationProviderImpl config;
    private PersistenceManagerV2Impl persistencemanager;
    private UserManagerImpl userManager;
    private FormatManagerImpl formatManager;
    private ConversationManagerImpl conversationManager;
    private PasteServiceManagerImpl pasteManager;
    private ShutdownManagerImpl shutdownManager;
    private MailManagerImpl mailManager;
    private RoleManagerImpl roleManager;
    private HttpManagerImpl httpManager;
    private WebInterfaceManagerImpl webInterface;
    private RunOnceManagerImpl runOnceManager;
    private EventProvider eventProvider;
    
    
    
    public MyPollyProvider(ModuleLoader loader) {
        super("MYPOLLY_PROVIDER", loader, true);
    }
    
    
    
    @Override
    public void beforeSetup() {
        this.commandManager = this.requireNow(CommandManagerImpl.class, true);
        this.ircManager = this.requireNow(IrcManagerImpl.class, true);
        this.pluginManager = this.requireNow(PluginManagerImpl.class, true);
        this.config = this.requireNow(ConfigurationProviderImpl.class, true);
        this.persistencemanager = this.requireNow(PersistenceManagerV2Impl.class, true);
        this.userManager = this.requireNow(UserManagerImpl.class, true);
        this.formatManager = this.requireNow(FormatManagerImpl.class, true);
        this.conversationManager = this.requireNow(ConversationManagerImpl.class, true);
        this.shutdownManager = this.requireNow(ShutdownManagerImpl.class, true);
        this.pasteManager = this.requireNow(PasteServiceManagerImpl.class, true);
        this.mailManager = this.requireNow(MailManagerImpl.class, true);
        this.roleManager = this.requireNow(RoleManagerImpl.class, true);
        this.httpManager = this.requireNow(HttpManagerImpl.class, true);
        this.webInterface = this.requireNow(WebInterfaceManagerImpl.class, true);
        this.runOnceManager = this.requireNow(RunOnceManagerImpl.class, true);
        this.eventProvider = this.requireNow(EventProvider.class, true);
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
            this.roleManager,
            this.httpManager,
            this.webInterface,
            this.runOnceManager,
            this.eventProvider);
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
        this.httpManager = null;
        this.webInterface = null;
        this.eventProvider = null;
        super.dispose();
    }
}
