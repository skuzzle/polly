package polly.configuration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.TreeSet;

import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationEvent;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationListener;
import de.skuzzle.polly.sdk.exceptions.DisposingException;


/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class PollyConfiguration extends Configuration 
        implements de.skuzzle.polly.sdk.Configuration, Disposable {
    
	private String logConfigFile;
	private String[] pluginExcludes;
	private String[] channels;
	private String[] ignoreCommands;
	private String nickName;
	private String ident;
	private String server;
	private int port;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	private String dbDriver;
	private boolean ircLogging;
	private String adminUserName;
	private String adminPasswordHash;
	private int adminUserlevel;
	private String declarationCache;
	private String dateFormatString;
	private String numberFormatString;
	private String ircModes;
	private int eventThreads;
	private int executionThreads;
	private String encodingName;
	private Charset encoding;
	private int lineLength;
	private int reconnectDelay;
	private int telnetPort;
	private boolean enableTelnet;
	private boolean autoUpdate;
	private String updateUrl;
	private String installerUpdateUrl;
	private int messageDelay;
	private boolean autoLogon;
	private int autoLogonTime;
	private boolean isDebugMode;
	private String persistenceXML;
	private String persistenceUnit;
    private String modulesCfg;
    private String keyStoreFile;
    private String keyStorePassword;
    private boolean ignoreUnknownIdentifiers;
    private int tempVarLifeTime;
	private List<ConfigurationListener> listener;
	
	public PollyConfiguration(String filename) 
	        throws IOException, ConfigurationFileException {
	    
		super(filename);
		this.listener = new LinkedList<ConfigurationListener>();
		this.init();
	}
	
	
	public PollyConfiguration() throws ConfigurationFileException {
	    super();
	    this.init();
	}
	
	
	public PollyConfiguration(PollyConfiguration defaults) 
	        throws ConfigurationFileException {
	    super(defaults.props);
	    this.init();
	}
	


	@Override
	protected void init() throws ConfigurationFileException {
		this.logConfigFile = this.props.getProperty(LOG_CONFIG_FILE, "cfg/logging.cfg");
        this.nickName = this.props.getProperty(NICKNAME, "polly");
        this.ident = this.props.getProperty(IDENT, "pollyp");
        this.server = this.props.getProperty(SERVER, "server");
        this.dbUrl = this.props.getProperty(DB_URL, "jdbc:hsqldb:file:./db/polly");
        this.dbUser = this.props.getProperty(DB_USER, "polly");
        this.dbPassword = this.props.getProperty(DB_PASSWORD, "polly123");
        this.dbDriver = this.props.getProperty(DB_DRIVER, "org.hsqldb.jdbcDriver");
        this.adminUserName = this.props.getProperty(ADMIN_NAME, "C0mb4t:");
        this.adminPasswordHash = this.props.getProperty(ADMIN_PASSWORD_HASH);
        this.declarationCache = this.props.getProperty(DECLARATION_CACHE, "cache/");
        this.dateFormatString = this.props.getProperty(DATE_FORMAT, "dd.MM.yyyy@HH:mm:ss");
        this.numberFormatString = this.props.getProperty(NUMBER_FORMAT, "0.####");
        this.encodingName = this.props.getProperty(ENCODING, "ISO-8859-1");
        this.updateUrl = this.props.getProperty(UPDATE_URL, "");
        this.installerUpdateUrl = this.props.getProperty(INSTALLER_UPDATE_URL, "");
        this.autoUpdate = this.readBoolean(this.props.getProperty(AUTO_UPDATE, "false"));
        this.ircModes = this.props.getProperty(IRC_MODES, "+B");
        this.isDebugMode = this.readBoolean(this.props.getProperty(DEBUG_MODE, "false"));
        this.persistenceUnit = this.props.getProperty(DB_PERSISTENCE_UNIT, "polly");
        this.persistenceXML = this.props.getProperty(DB_PERSISTENCE_XML_PATH, 
            "cfg/META-INF/persistence.xml");
        this.modulesCfg = this.props.getProperty(MODULE_CONFIG, "cfg/modules.cfg");
        this.keyStoreFile = this.props.getProperty(KEYSTORE_FILE, "cfg/SSLKeyStore");
        this.keyStorePassword = this.props.getProperty(KEYSTORE_PASSWORD, "");
        
        try {
            this.encoding = Charset.forName(this.encodingName);
        } catch (Exception e) {
            throw new ConfigurationFileException("Invalid encoding: " + 
                this.encodingName);
        }
        
        try {
            if (!this.updateUrl.equals("")) {
                new URL(this.updateUrl);
            }
            if (!this.installerUpdateUrl.equals("")) {
                new URL(this.installerUpdateUrl);
            }
        } catch (MalformedURLException e) {
            throw new ConfigurationFileException("Invalid update url: " + this.updateUrl);
        }
        
        String tmp = this.props.getProperty(PLUGIN_EXCLUDES, "");
        this.pluginExcludes = tmp.split(",");
        
        tmp = this.props.getProperty(CHANNELS, "");
        this.channels = tmp.split(",");
        
        tmp = this.props.getProperty(IGNORED_COMMANDS, "");
        this.ignoreCommands = tmp.split(",");
        
        tmp = this.props.getProperty(PORT, "6669");
        this.port = this.parseInteger(tmp, 32, "Ungültige Portnummer");
        
        tmp = this.props.getProperty(IRC_LOGGING, "on");
        this.ircLogging = this.readBoolean(tmp);
        
        tmp = this.props.getProperty(ADMIN_USER_LEVEL, "1337");
        this.adminUserlevel = this.parseInteger(tmp, 1, "Ungültiges Admin User Level.");
        
        tmp = this.props.getProperty(EVENT_THREADS, "4");
        this.eventThreads = this.parseInteger(tmp, 1, "Ungültige Eventthread Zahl.");
        
        tmp = this.props.getProperty(EXECUTION_THREADS, "4");
        this.executionThreads = this.parseInteger(tmp, 1, "Ungültige Executionthread Zahl.");
        
        tmp = this.props.getProperty(LINE_LENGTH, "300");
        this.lineLength = this.parseInteger(tmp, 200, "Attribut lineLength zu kurz.");
        
        tmp = this.props.getProperty(RECONNECT_DELAY, "2000");
        this.reconnectDelay = this.parseInteger(tmp, 1000, "Reconnect delay zu niedrig.");
        
        tmp = this.props.getProperty(ENABLE_TELNET, "true");
        this.enableTelnet = this.readBoolean(tmp);
        
        tmp = this.props.getProperty(TELNET_PORT, "23");
        this.telnetPort = this.parseInteger(tmp, 0, "Ungültige Portnummer.");
        
        tmp = this.props.getProperty(MESSAGE_DELAY, "250");
        this.messageDelay = this.parseInteger(tmp, 250, "Message delay zu niedrig.");
        
        tmp = this.props.getProperty(AUTO_LOGIN, "true");
        this.autoLogon = this.readBoolean(tmp);
        
        tmp = this.props.getProperty(AUTO_LOGIN_TIME, "60000");
        this.autoLogonTime = this.parseInteger(tmp, 4999, "autoLoginTime sollte mind. " +
        		"5000 sein.");
        
        tmp = this.props.getProperty(TEMP_VAR_LIFETIME, "30000");
        this.tempVarLifeTime = this.parseInteger(tmp, 0, "");
        
        tmp = this.props.getProperty(IGNORE_UNKNOWN_IDENTIFIERS, "true");
        this.ignoreUnknownIdentifiers = this.readBoolean(tmp);
	}
	
	
	
	
    public int getTempVarLifeTime() {
        return this.tempVarLifeTime;
    }
    
    
    
    
    public boolean isIgnoreUnknownIdentifiers() {
        return this.ignoreUnknownIdentifiers;
    }
		
	
	
    public String getKeyStoreFile() {
        return this.keyStoreFile;
    }
    
    
    
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }
    
	
	
    public String getModulesCfg() {
        return this.modulesCfg;
    }

	
	public String[] getPluginExcludes() {
		return pluginExcludes;
	}
	
	
	
	public void setPluginExcludes(String excludes) {
	    this.pluginExcludes = excludes.split(",");
	    this.props.setProperty(PLUGIN_EXCLUDES, excludes);
	}


	
	public String getNickName() {
		return nickName;
	}


	
	public String getIdent() {
		return ident;
	}
	
	
	
	public String getServer() {
		return server;
	}
	
	

	public int getPort() {
		return port;
	}
	
	
	public String[] getChannels() {
		return this.channels;
	}

	

	public String getLogConfigFile() {
		return logConfigFile;
	}

	

	public String getDbUrl() {
        return dbUrl;
    }



    public String getDbUser() {
        return dbUser;
    }



    public String getDbPassword() {
        return dbPassword;
    }



    public String getDbDriver() {
        return dbDriver;
    }
    
    
    
    public String getPersistenceUnit() {
        return this.persistenceUnit;
    }
    
    

    public String getPersistenceXML() {
        return this.persistenceXML;
    }

    
    
    public boolean getIrcLogging() {
        return this.ircLogging;
    }
    
    
    
    
    public String getIrcModes() {
        return this.ircModes;
    }
    
    
    
    public String getAdminUserName() {
        return this.adminUserName;
    }
    
    
    
    public String getAdminPasswordHash() {
        return this.adminPasswordHash;
    }
    
    
    
    public int getAdminUserLevel() {
        return this.adminUserlevel;
    }
    
    
    
    public String getDeclarationCachePath() {
        return this.declarationCache;
    }
    
    
    
    public String getDateFormatString() {
        return this.dateFormatString;
    }
    
    
    
    public String getNumberFormatString() {
        return this.numberFormatString;
    }
    
    
    
    public int getEventThreads() {
        return this.eventThreads;
    }
    
    
    
    public int getExecutionThreads() {
        return this.executionThreads;
    }
    
    
    
    public String[] getIgnoredCommands() {
        return this.ignoreCommands;
    }
    
    
    
    public String getEncodingName() {
        return this.encodingName;
    }
    
    
    
    public Charset getEncoding() {
        return this.encoding;
    }

    
    
    public int getLineLength() {
        return this.lineLength;
    }
    
    
    
    public int getReconnectDelay() {
        return this.reconnectDelay;
    }
    
    
    public boolean enableTelnet() {
        return this.enableTelnet;
    }
    
    
    
    public int getTelnetPort() {
        return this.telnetPort;
    }

    
    
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }
    
    
    public String getUpdateUrl() {
        return this.updateUrl;
    }
    
    
    
    public String getInstallerUpdateUrl() {
        return this.installerUpdateUrl;
    }
    
    
    
    public int getMessageDelay() {
        return this.messageDelay;
    }
    
    
    
    
    public int getAutoLogonTime() {
        return this.autoLogonTime;
    }
    
    
    
    public boolean isAutoLogon() {
        return this.autoLogon;
    }
    
    
    
    public boolean isDebugMode() {
        return this.isDebugMode;
    }
    
    
    
    @Override
	public synchronized <T> void setProperty(String name, T value) {
		this.props.setProperty(name, value.toString());
		/*
		 * HACK: update all 'static' config fields.
		 */
		try {
		    this.init();
		} catch (ConfigurationFileException e) {
		    e.printStackTrace();
		}
	}


	
	@Override
	public String readString(String name) {
		return this.readString(name, "");
	}

	

	@Override
	public int readInt(String name) {
		 return this.readInt(name, 0);
	}


	
	@Override
	public synchronized String readString(String name, String defaultValue) {
		return this.props.getProperty(name, defaultValue);
	}


	
	@Override
	public synchronized int readInt(String name, int defaultValue) {
		String tmp = this.props.getProperty(name);
		int i = defaultValue;
		try {
			i = Integer.parseInt(tmp);
		} catch (Exception ignore) {}
		
		return i;	
	}
	
	
	@Override
	public void addConfigurationListener(ConfigurationListener listener) {
	    this.listener.add(listener);
	}
	
	
	
	@Override
	public void removeConfigurationListener(ConfigurationListener listener) {
	    this.listener.remove(listener);
	}
	
	
	
	@Override
	public void fireConfigurationChanged() {
	    ConfigurationEvent e = new ConfigurationEvent(this);
	    for (ConfigurationListener listener : this.listener) {
	        listener.configurationChange(e);
	    }
	}

	
	@Override
	protected void actualDispose() throws DisposingException {
	    // Do not store cfg here, because it contains changes made by program parameters
	    
	    /*if (!this.isMemory()) {
	        try {
                this.store();
            } catch (IOException e) {
                throw new DisposingException(e);
            }
	    }*/
	}

    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        Comparator<Entry<Object, Object>> c = new Comparator<Entry<Object,Object>>() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(Entry<Object, Object> o1,
                        Entry<Object, Object> o2) {
                return ((Comparable<Object>) o1.getKey()).compareTo(o2.getKey());
            }
        };
        
        SortedSet<Entry<Object, Object>> sorted = new TreeSet<Entry<Object,Object>>(c);
        sorted.addAll(this.props.entrySet());
        for (Entry<Object, Object> entry : sorted) {
            b.append(entry.getKey());
            b.append(" = ");
            b.append(entry.getValue());
            b.append("\n");
        }
        return b.toString();
    }
}
