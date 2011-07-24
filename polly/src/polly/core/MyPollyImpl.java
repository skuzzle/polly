package polly.core;

import java.util.Date;

import org.apache.log4j.Logger;

import polly.Polly;
import polly.PollyConfiguration;
import polly.events.EventProvider;


import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.PluginManager;
import de.skuzzle.polly.sdk.UserManager;



public class MyPollyImpl implements MyPolly, Disposable {
	
	private static Logger logger = Logger.getLogger(MyPollyImpl.class.getName());
	
	private CommandManagerImpl commandManager;
	private IrcManagerImpl ircManager;
	private PluginManagerImpl pluginManager;
	private PollyConfiguration config;
	private PersistenceManagerImpl persistence;
	private UserManagerImpl userManager;
	private FormatManagerImpl formatManager;
	private EventProvider eventProvider;
	private Polly polly;
	private Date startTime;
	
	
	
	public MyPollyImpl(CommandManagerImpl cmdMngr, 
	        IrcManagerImpl ircMngr, 
			PluginManagerImpl plgnMngr, 
			PollyConfiguration config, 
			PersistenceManagerImpl pMngr,
			UserManagerImpl usrMngr,
			FormatManagerImpl fmtMngr,
			EventProvider eventProvider,
			Polly polly) {
	    
		this.commandManager = cmdMngr;
		this.ircManager = ircMngr;
		this.pluginManager = plgnMngr;
		this.config = config;
		this.persistence = pMngr;
		this.userManager = usrMngr;
		this.formatManager = fmtMngr;
		this.eventProvider = eventProvider;
		this.polly = polly;
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
	    this.shutdown(true);
	}
	
	
	
	public void shutdown(boolean exit) {
        logger.info("Preparing to shutdown...");
        this.pluginManager.dispose();
        this.ircManager.dispose();
        this.userManager.dispose();
        this.persistence.dispose();
        this.config.dispose();
        
        logger.info("Shutting down event thread pool.");
        eventProvider.dispose();
        
        this.polly.shutdown();
        
        logger.trace("Remaining active threads: " + Thread.activeCount());
        logger.trace("Remaining stacktraces:");
        Thread[] active = new Thread[Thread.activeCount()];
        Thread.enumerate(active);
        for (Thread t : active) {
            logger.trace("Thread: " + t.toString());
            for (StackTraceElement e : t.getStackTrace()) {
                logger.trace("    " + e.toString());
            }
        }
        
        if (exit) {
            logger.info("All connections closed. Now exiting the whole program. ByeBye");
            System.exit(0);
        }
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
    
    
    
    public Date getStartTime() {
        return this.startTime;
    }



    @Override
    public void dispose() {
        this.shutdown();
    }
}
