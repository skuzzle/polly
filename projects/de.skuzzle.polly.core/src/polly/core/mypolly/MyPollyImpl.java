package polly.core.mypolly;

import java.util.Date;


import polly.Polly;
import polly.configuration.ConfigurationProviderImpl;
import polly.core.ShutdownManagerImpl;
import polly.core.commands.CommandManagerImpl;
import polly.core.conversations.ConversationManagerImpl;
import polly.core.formatting.FormatManagerImpl;
import polly.core.http.HttpManagerImpl;
import polly.core.irc.IrcManagerImpl;
import polly.core.mail.MailManagerImpl;
import polly.core.paste.PasteServiceManagerImpl;
import polly.core.persistence.PersistenceManagerImpl;
import polly.core.plugins.PluginManagerImpl;
import polly.core.roles.RoleManagerImpl;
import polly.core.users.UserManagerImpl;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.ConversationManager;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MailManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.PluginManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.UtilityManager;
import de.skuzzle.polly.sdk.eventlistener.GenericEvent;
import de.skuzzle.polly.sdk.eventlistener.GenericListener;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.http.HttpManager;
import de.skuzzle.polly.sdk.paste.PasteServiceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.events.Dispatchable;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.Listeners;



/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class MyPollyImpl extends AbstractDisposable implements MyPolly {
    
	private CommandManagerImpl commandManager;
	private IrcManagerImpl ircManager;
	private PluginManagerImpl pluginManager;
	private ConfigurationProviderImpl configProvider;
	private PersistenceManagerImpl persistence;
	private UserManagerImpl userManager;
	private FormatManagerImpl formatManager;
	private ConversationManagerImpl conversationManager;
	private ShutdownManagerImpl shutdownManager;
	private Date startTime;
	private PasteServiceManagerImpl pasteManager;
	private MailManagerImpl mailManager;
	private RoleManagerImpl roleManager;
	private HttpManagerImpl httpManager;
	private EventProvider eventProvider;
	
	
	public MyPollyImpl(CommandManagerImpl cmdMngr, 
	        IrcManagerImpl ircMngr, 
			PluginManagerImpl plgnMngr, 
			ConfigurationProviderImpl configProviderImpl, 
			PersistenceManagerImpl pMngr,
			UserManagerImpl usrMngr,
			FormatManagerImpl fmtMngr,
			ConversationManagerImpl convMngr,
			ShutdownManagerImpl shutdownManager,
			PasteServiceManagerImpl pasteManager,
			MailManagerImpl mailManager,
			RoleManagerImpl roleManager,
			HttpManagerImpl httpManager,
			EventProvider eventProvider) {
	    
		this.commandManager = cmdMngr;
		this.ircManager = ircMngr;
		this.pluginManager = plgnMngr;
		this.configProvider = configProviderImpl;
		this.persistence = pMngr;
		this.userManager = usrMngr;
		this.formatManager = fmtMngr;
		this.conversationManager = convMngr;
		this.shutdownManager = shutdownManager;
		this.pasteManager = pasteManager;
		this.startTime = Time.currentTime();
		this.mailManager = mailManager;
		this.roleManager = roleManager;
		this.httpManager = httpManager;
	}
	
	

	@Override
	public IrcManager irc() {
		return this.ircManager;
	}

	
	
	@Override
	public String getPollyVersion() {
		return Polly.class.getPackage().getImplementationVersion();
	}

	
	
	@Override
	public HttpManager web() {
	    return this.httpManager;
	}
	
	
	@Override
	public PersistenceManager persistence() {
		return this.persistence;
	}

	
	
	@Override
	public UserManager users() {
		return this.userManager;
	}


	@Override
	public CommandManager commands() {
		return this.commandManager;
	}



	@Override
	public PluginManager plugins() {
		return this.pluginManager;
	}



	@Override
	public synchronized void shutdown() {
	    this.shutdownManager.shutdown();
	}
	
	
	@Override
	public ShutdownManagerImpl shutdownManager() {
	    return this.shutdownManager;
	}
	
	
	
	@Deprecated
	public void shutdown(boolean exit) {
	    this.shutdownManager.shutdown(exit);
	}



	@Override
	public ConfigurationProvider configuration() {
		return this.configProvider;
	}

	
	
	@Override
	public PasteServiceManager pasting() {
	    return this.pasteManager;
	}
	
	
	
	@Override
	public UtilityManager utilities() {
	    return new UtilityManager() {/* TODO: utilities */};
	}
	

	@Override
	public String getLoggerName(Class<?> clazz) {
		return "polly.Plugin." + clazz.getName();
	}



    @Override
    public FormatManager formatting() {
        return this.formatManager;
    }
    
    
    
    @Override
    public ConversationManager conversations() {
        return this.conversationManager;
    }
    
    
    
    public Date getStartTime() {
        return this.startTime;
    }

    
    
    public boolean isDebugMode() {
        return this.configProvider.getRootConfiguration().readBoolean(
            Configuration.DEBUG_MODE);
    }
    
    
    
    @Override
    public MailManager mails() {
        return this.mailManager;
    }
    

    @Override
    protected void actualDispose() throws DisposingException {
        this.shutdown();
    }



    @Override
    public RoleManager roles() {
        return this.roleManager;
    }



    @Override
    public void addGenericListener(GenericListener listener) {
        this.eventProvider.addListener(GenericListener.class, listener);
    }



    @Override
    public void removeGenericListener(GenericListener listener) {
        this.eventProvider.removeListener(GenericListener.class, listener);
    }



    @Override
    public void fireGenericEvent(final GenericEvent e) {
        Listeners<GenericListener> listeners = this.eventProvider.getListeners(
            GenericListener.class);
        
        Dispatchable<GenericListener, GenericEvent> d = 
            new Dispatchable<GenericListener, GenericEvent>(listeners, e) {
                @Override
                public void dispatch(GenericListener listener,
                        GenericEvent event) {
                    listener.genericEvent(e);
                }
        };
        this.eventProvider.dispatchEvent(d);
    }
}
