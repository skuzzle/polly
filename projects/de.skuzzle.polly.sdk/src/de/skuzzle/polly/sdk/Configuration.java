package de.skuzzle.polly.sdk;

import java.util.List;


/**
 * <p>Provides access to the polly configuration file.</p>
 * 
 * <p>You can access all polly configuration properties, but there may come up 
 * restrictions in further polly versions for security reasons. So far, you have
 * read-only access to all properties and can retrieve them by passing any of the
 * static constants to the read methods.</p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface Configuration {
    
    /**
     * Config file property name which points the path to the log4j configuration file
     * @since Beta 0.5
     */
    public final static String LOG_CONFIG_FILE = "loggerSettings"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to a comma seperated list of plugin names
     * that are to be excluded upon loading.
     * @since Beta 0.5
     */
    public final static String PLUGIN_EXCLUDES = "pluginExcludes"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to a comma seperated list of channel names
     * that are to be joined upon starting.
     * @since Beta 0.5
     */
    public final static String CHANNELS = "channels"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the irc-nickname of polly.
     * @since Beta 0.5
     */
    public final static String NICKNAME = "nickName"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the nickserv password.
     * @since Beta 0.5
     */
    public final static String IDENT = "ident"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the irc server address to connect to
     * on startup.
     * @since Beta 0.5
     */
    public final static String SERVER = "server"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to a list of possible ports for the
     * {@link #SERVER}.
     * @since Beta 0.5
     */
    public final static String PORT = "ports"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the database url. You should not change
     * this config property!  lease Note that further polly version may restrict 
     * access to this field.
     * @since Beta 0.5
     */
    public final static String DB_URL = "dbUrl"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the database username. You should not 
     * change this config property! Please Note that further polly version may restrict 
     * access to this field.
     * @since Beta 0.5
     */
    public final static String DB_USER = "dbUser"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the database password. You should not
     * change this config property! Please Note that further polly version may restrict 
     * access to this field.
     * @since Beta 0.5
     */
    public final static String DB_PASSWORD = "dbPassword"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the database driver name.
     * @since Beta 0.5
     */
    public final static String DB_DRIVER = "dbDriver"; //$NON-NLS-1$
    
    /**
     * The name of the persistenceunit to use. This is almost always 'polly'
     * @since 0.7
     */
    public final static String DB_PERSISTENCE_UNIT = "persistenceUnit"; //$NON-NLS-1$
    
    /**
     * The path of the persistence.xml. This is almost always 
     * "cfg/META-INF/persistence.xml".
     * @since 0.7
     */
    public final static String DB_PERSISTENCE_XML_PATH = "persistenceXML"; //$NON-NLS-1$
    
    /**
     * Config file property name which enables/disables logging of irc messages.
     * @since Beta 0.5
     */
    public final static String IRC_LOGGING = "ircLogging"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the name of the admin user.  You should 
     * not change this config property! Please Note that further polly version may 
     * restrict access to this field.
     * @since Beta 0.5
     */
    public final static String ADMIN_NAME = "adminUserName"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the admin password which is not stored
     * as plaintext but as MD5 hash. You should not change this config property! 
     * Please Note that further polly version may restrict access to this field.
     * @since Beta 0.5
     */
    public final static String ADMIN_PASSWORD_HASH = "adminPasswordHash"; //$NON-NLS-1$
    
    /**
     * Config file property name which sets the user level for the admin user. You should
     * not change this property. If you do so, ensure to net set it lower than 
     * {@link UserManager#ADMIN}. Please note that further polly version may restrict 
     * access to this field.
     * @since Beta 0.5
     */
    public final static String ADMIN_USER_LEVEL = "adminUserLevel"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the path where userdefined variable
     * declarations are stored.
     * @since Beta 0.5
     */
    public final static String DECLARATION_CACHE = "declarationCache"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the global date format pattern.
     * @since Beta 0.5
     */
    public final static String DATE_FORMAT = "dateFormat"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the global number format pattern.
     * @since Beta 0.5
     */
    public final static String NUMBER_FORMAT = "numberFormat"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to maximum count of event threads.
     * @since Beta 0.5
     */
    public final static String EVENT_THREADS = "eventThreads"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to a comma separated list of command names
     * which will be ignored when trying to register them.
     * @since Beta 0.5
     */
    public final static String IGNORED_COMMANDS ="ignoreCommands"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the global encoding polly uses for 
     * incoming as well as outgoing messages.
     * @since Beta 0.5
     */
    public final static String ENCODING = "encoding"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the maximum line length for outgoing
     * irc messages. Polly automatically wraps lines to this length.
     * @since Beta 0.5
     */
    public final static String LINE_LENGTH = "lineLength"; //$NON-NLS-1$
    
    /**
     * Config file property name which refers to the delay which polly waits when trying
     * to reestablish a lost connection. Given in milliseconds.
     * @since Beta 0.5
     */
    public final static String RECONNECT_DELAY = "reconnectDelay"; //$NON-NLS-1$
    
    /**
     * Config file property name which enables/disables the polly telnet server feature.
     * @since Beta 0.5
     */
    public final static String ENABLE_TELNET = "enableTelnet"; //$NON-NLS-1$
    
    /**
     * Config file property name which sets the port for the polly telnet server (if 
     * enabled).
     * @since Beta 0.5
     */
    public final static String TELNET_PORT = "telnetPort"; //$NON-NLS-1$
    
    /**
     * Config file property which sets the update url for polly.
     * @since Beta 0.6.0
     */
    public final static String UPDATE_URL = "updateUrl"; //$NON-NLS-1$
    
    /**
     * Config file property which sets the update url for the update installer.
     * @since 0.6.0
     */
    public final static String INSTALLER_UPDATE_URL = "installerUpdateUrl"; //$NON-NLS-1$
    
    /**
     * Config file property which enables auto updating.
     * @since Beta 0.6.0
     */
    public final static String AUTO_UPDATE = "autoUpdate"; //$NON-NLS-1$
    
    /**
     * Sets the minimum delay between outgoing irc messages.
     * @since 0.6.1
     */
    public final static String MESSAGE_DELAY = "messageDelay"; //$NON-NLS-1$
    
    /**
     * Sets how many threads should be used for command execution. As of polly version
     * 0.6.1 event threads and execution threads have been separated.
     * @since 0.6.1
     */
    public final static String EXECUTION_THREADS = "executionThreads"; //$NON-NLS-1$
    
    /**
     * Sets the time in milliseconds after which a user gets automatically logged on
     * after he has been spotted. This only takes effect if {@link #AUTO_LOGIN} is set
     * to <code>true</code>.
     * @since 0.6.1
     */
    public final static String AUTO_LOGIN_TIME = "autoLoginTime"; //$NON-NLS-1$
    
    /**
     * Enables/Disables auto login for registered users.
     * @since 0.6.1
     */
    public final static String AUTO_LOGIN = "autoLogin"; //$NON-NLS-1$
    
    /**
     * Sets the irc modes to set after connecting.
     * @since 0.6.1
     */
    public final static String IRC_MODES = "ircModes"; //$NON-NLS-1$
    
    /**
     * Sets whether polly is running in debug mode. This is a boolean field.
     * @since 0.7
     */
    public final static String DEBUG_MODE = "debugMode"; //$NON-NLS-1$
    
    /**
     * Path to the modules cfg which contains the full qualified name of each module
     * which will be loaded by polly upon start.
     * @since 0.7
     */
    public final static String MODULE_CONFIG = "modulesCfg"; //$NON-NLS-1$
    
    /**
     * Path to the keystore file which stores the certificate for the remote 
     * administration servers SSL socket.
     * @since 0.7
     */
    public final static String KEYSTORE_FILE = "keyStore"; //$NON-NLS-1$
    
    /**
     * Password of the keystore file.
     * @since 0.7
     */
    public final static String KEYSTORE_PASSWORD = "keyStorePW"; //$NON-NLS-1$
    
    /**
     * Password for the certificate
     * @since 0.9
     */
    public final static String KEY_PASSWORD = "keyPW"; //$NON-NLS-1$
    
    /**
     * The lifetime for temporary variables in milliseconds.
     * @since 0.8
     */
    public final static String TEMP_VAR_LIFETIME = "tempVarLifeTime"; //$NON-NLS-1$
    
    /**
     * Sets whether unknown identifiers are automatically treated as String literals
     * of the identifiers name.
     * @since 0.8
     */
    public final static String IGNORE_UNKNOWN_IDENTIFIERS = "ignoreUnknownIdentifiers"; //$NON-NLS-1$
    
    /**
     * Sets the detail level for reported parse errors: 0 means off, 1 means simple and
     * every value greater than 2 means detailed.
     * @since 0.9
     */
    public final static String PARSE_ERROR_DETAILS = "parseErrorDetails"; //$NON-NLS-1$
    
    /**
     * Number of connection rety for the initial connect before polly gives up and fails
     * loading.
     * @since 0.9.1
     */
    public final static String INITIAL_RETY_COUNT = "initialRetryCount"; //$NON-NLS-1$
    
    /**
     * Timeout for HTTP sessions.
     * @since 0.9.1
     */
    public final static String HTTP_SESSION_TIMEOUT = "sessionTimeOut"; //$NON-NLS-1$
    
    /**
     * The public host name under which the web interface will be reachable.
     * @since 0.9.1
     */
    public final static String HTTP_PUBLIC_HOST = "publicHost"; //$NON-NLS-1$
    
    /**
     * The port for the HTTP server.
     * @since 0.9.1
     */
    public final static String HTTP_PORT = "port"; //$NON-NLS-1$
    
    /**
     * Root directory for html template files.
     * @since 0.9.1
     */
    public final static String HTTP_TEMPLATE_ROOT = "templateRoot"; //$NON-NLS-1$
    
    /**
     * Determines whether the server will be started when polly
     * starts.
     * @since 0.9.1
     */
    public final static String HTTP_START_SERVER = "startServer"; //$NON-NLS-1$
    
    /**
     * Number of session cache actions before the cache is cleaned up from
     * timed out sessions.
     * @since 0.9.1
     */
    public final static String HTTP_SESSION_CACHE_THRESHOLD = "cacheThreshold"; //$NON-NLS-1$
    
    /**
     * Whether polly should output an error message when a user tries to execute a 
     * command that does not exist.
     * @since 0.9.1
     */
    public final static String REPORT_UNKNOWN_COMMAND_ERROR = "reportUnknownCommandError"; //$NON-NLS-1$
    
    /**
     * Time after which a session will be marked as timed out if no user logged in on it.
     * @since 0.9.1
     */
    public static final String HTTP_LOGIN_TIMEOUT = "loginTimeOut"; //$NON-NLS-1$

    /**
     * Threshold value of invalid http requests before a http session will be blocked for
     * security purposes.
     * @since 0.9.1
     */
    public static final String HTTP_ERROR_THRESHOLD = "errorThreshold"; //$NON-NLS-1$

    
    /**
     * When set to <code>true</code>, polly joins the default channels immediately
     * after being connected. Otherwise, the channels will be joined after
     * nickserv identification.
     */
    public static final String JOIN_ON_CONNECT = "joinOnConnect"; //$NON-NLS-1$

    /**
     * Whether the webserver should use SSL
     */
    public static final String HTTP_USE_SSL = "useSSL"; //$NON-NLS-1$
    
    
    /** 
     * The key which refers to the locale to use by polly.
     * @since 1.1
     */
    public final static String LOCALE = "locale"; //$NON-NLS-1$
    
    
    
	/**
	 * <p>Stores a property with given name and value. If a property with the same name
	 * existed before, it will be overridden. If it not existed, it will be created.</p>
	 * 
	 * @param name The property name.
	 * @param value The properties value. This will be stored as a String, calling its
	 * 		<code>toString</code> method.
	 */
	public abstract <T> void setProperty(String name, T value);
	
	
	
	/**
	 * Reads a String from the properties. If no property with the given name exists,
	 * an empty String is returned.
	 * 
	 * @param name The name of the property to read.
	 * @return The properties value as a String.
	 */
	public abstract String readString(String name);
	
	
	
	/**
	 * Reads a String from the properties. If no property with the given name exists,
	 * the defaultValue parameter is returned.
	 * 
	 * @param name The name of the property to read.
	 * @param defaultValue The default value to return if the property does not exist.
	 * @return The properties value as a String.
	 */
	public abstract String readString(String name, String defaultValue);
	
	
	
	/**
	 * Reads an int from the properties. If no property with the given name exists or the
	 * value forms no valid integer, 0 is returned.
	 * 
	 * @param name The name of the property to read.
	 * @return The properties value as an int.
	 */
	public abstract int readInt(String name);
	
	
	
	/**
	 * Reads an int from the properties. If no property with the given name exists or the
	 * value forms no valid integer, the defaultValue parameter is returned.
	 * 
	 * @param name The name of the property to read.
	 * @param defaultValue The default value to return if the property does not exist.
	 * @return The properties value as an int.
	 */
	public abstract int readInt(String name, int defaultValue);
	
	
	
	/**
	 * Reads a boolean value from the configuration. That ist, the value of the property
	 * must either be <code>true</code> or <code>false</code>.
	 * @param name The property name.
	 * @return The corresponding boolean value.
	 */
	public abstract boolean readBoolean(String name);
	
	
	
	/**
	 * Reads a list of strings from the configuration. Lists are normal properties 
	 * separated by ",".
	 * 
	 * @param name The property name.
	 * @return A list of strings.
	 */
	public abstract List<String> readStringList(String name);
	
	
	
    /**
     * Reads a list of integers from the configuration. Lists are normal properties 
     * separated by ",".
     * 
     * @param name The property name.
     * @return A list of integers.
     */
	public List<Integer> readIntList(String name);



	/**
	 * Gets the validator instance with which this configuration was validated. Check
	 * {@link #isValidated()} before calling this to ensure that this instance was
	 * validated.
	 * 
	 * @return The validator of this configuration or <code>null</code> if this 
	 *             configuration was not validated.
	 */
    public ConfigurationValidator getValidator();
    
    
    
    /**
     * Returns whether this configuration was validated.
     * 
     * @return <code>true</code> iff this config was validated when it was opened.
     */
    public boolean isValidated();
}