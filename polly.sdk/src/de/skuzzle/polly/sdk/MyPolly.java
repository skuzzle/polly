package de.skuzzle.polly.sdk;

import java.util.Date;

import de.skuzzle.polly.sdk.time.TimeProvider;


/**
 * Main interface class which provides access to all available polly features you can 
 * use in your plugin.
 * 
 * @author Simon
 * @since zero day
 * @version 27.07.2011 0366137
 */
public interface MyPolly {

	/**
	 * Gets the currently used polly version.
	 * @return The polly version as String.
	 */
	public abstract String getPollyVersion();
	
	
	
	/**
	 * Returns a log4j logger String for plugins. If you use log4j in your plugins,
	 * you can use this method to generate polly compatible logger names.
	 * 
	 * <pre>
	 * Logger logger = Logger.getLogger(myPolly.getLoggerName(YourClass.class));
	 * </pre>
	 * 
	 * @param clazz The class instance for which this logger is for
	 * @return The plugin root logger name.
	 */
	public abstract String getLoggerName(Class<?> clazz);
	
	
	
	/**
	 * Gets the UserManager which is responsible for adding,deleting and managing users.
	 * @return A UserManager instance.
	 */
	public abstract UserManager users();
	
	
	
	/**
	 * Gets the CommandManager which is responsible for all irc related tasks such as
	 * sending messages to channels/users or join/part channels.
	 * @return An IrcManager instance.
	 */
	public abstract IrcManager irc();
	
	
	
	/**
	 * Gets the PersistenceManager which is the interface to the Polly Persistence API.
	 * @return The persistenceManager.
	 */
	public abstract PersistenceManager persistence();
	
	
	
	/**
	 * Gets the CommandManager which is responsible for all command related tasks such as
	 * registering new commands.
	 * @return A CommandManager instance.
	 */
	public abstract CommandManager commands();
	
	
	
	/**
	 * Gets the PluginManager which is responsible for all plugin related tasks such as
	 * loading/unloading plugins.
	 * @return A PluginManager instance.
	 */
	public abstract PluginManager plugins();
	
	
	
	/**
	 * Gets the ConfigurationManager to access pollys configuration file.
	 * @return A Configuration instance.
	 */
	public abstract Configuration configuration();
	
	
	/**
	 * Gets the FormatManager to format Strings and Dates consistently.
	 * @return A FormatManager instance.
	 */
	public abstract FormatManager formatting();
	
	
	
	/**
	 * Gets the time where current polly instance has been started. 
	 * @return A Date object representing pollys start time.
	 * @since Beta 0.5
	 */
	public abstract Date getStartTime();

	
	
	/**
	 * Gets the ShutdownManager to safely restart and shutdown polly.
	 * @return A ShutdownManager instance.
	 * @since Beta 0.5
	 */
	public abstract ShutdownManager shutdownManager();
	
	
	
	/**
	 * Performs a clean shutdown of polly.
	 * @deprecated Use {@link ShutdownManager#shutdown()} instead.
	 * @see #shutdownManager()
	 */
	@Deprecated
	public abstract void shutdown();
	
	
	/**
	 * Gets the conversation manager for creating {@link Conversation}s.
	 * 
	 * @return The conversation manager.
	 * @since 0.6
	 */
	public abstract ConversationManager conversations();
	
	
	/**
	 * Sets the time provider for the polly system time. This method will have no effect
	 * if {@link Configuration#DEBUG_MODE} is "off".
	 * 
	 * @param timeProvider The TimeProvider which provides the current system time.
	 * @since 0.7
	 */
	public abstract void setTimeProvider(TimeProvider timeProvider);
	
	/**
	 * Gets the current polly system time provider.
	 * 
	 * @return The current time provider.
	 * @since 0.7
	 */
	public abstract TimeProvider getTimeProvider();
	
	/**
	 * Gets the current "polly"-system time. In productive systems, this will always
	 * be equals to {@link System#currentTimeMillis()}. For debugging purposes polly 
	 * might return different times.
	 * 
	 * When should use this method for all your time dependent applications.
	 * 
	 * @return Current system time as returned by {@link System#currentTimeMillis()}.
	 * @since 0.7
	 */
	public abstract long currentTimeMillis();
	
	
	/**
	 * Gets a Date object constructed with the current polly system time.
	 * 
	 * @return A Date object representing the current polly system time.
	 * @since 0.7
	 * @see #currentTimeMillis()
	 */
	public abstract Date pollySystemTime();
}
