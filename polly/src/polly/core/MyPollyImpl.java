package polly.core;

import java.util.Date;

import polly.Polly;


import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConversationManager;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.PluginManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DisposingException;



/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class MyPollyImpl extends AbstractDisposable implements MyPolly {

	private CommandManagerImpl commandManager;
	private IrcManagerImpl ircManager;
	private PluginManagerImpl pluginManager;
	private Configuration config;
	private PersistenceManagerImpl persistence;
	private UserManagerImpl userManager;
	private FormatManagerImpl formatManager;
	private ConversationManagerImpl conversationManager;
	private Date startTime;
	
	
	
	public MyPollyImpl(CommandManagerImpl cmdMngr, 
	        IrcManagerImpl ircMngr, 
			PluginManagerImpl plgnMngr, 
			Configuration config, 
			PersistenceManagerImpl pMngr,
			UserManagerImpl usrMngr,
			FormatManagerImpl fmtMngr,
			ConversationManagerImpl convMngr) {
	    
		this.commandManager = cmdMngr;
		this.ircManager = ircMngr;
		this.pluginManager = plgnMngr;
		this.config = new ConfigurationWrapper(config);
		this.persistence = pMngr;
		this.userManager = usrMngr;
		this.formatManager = fmtMngr;
		this.conversationManager = convMngr;
		this.startTime = new Date();
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
	    ShutdownManagerImpl.get().shutdown();
	}
	
	
	
	public ShutdownManagerImpl shutdownManager() {
	    return ShutdownManagerImpl.get();
	}
	
	
	
	@Deprecated
	public void shutdown(boolean exit) {
	    ShutdownManagerImpl.get().shutdown(exit);
	}



	@Override
	public Configuration configuration() {
		return this.config;
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


    @Override
    protected void actualDispose() throws DisposingException {
        this.shutdown();
    }
}
