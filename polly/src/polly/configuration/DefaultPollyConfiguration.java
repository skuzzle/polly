package polly.configuration;


import polly.SortedProperties;
import polly.data.User;
import de.skuzzle.polly.sdk.Configuration;


public class DefaultPollyConfiguration extends SortedProperties {

    private static final long serialVersionUID = 1L;



    public DefaultPollyConfiguration() {
        this.setProperty(Configuration.ADMIN_NAME, "admin");
        this.setProperty(Configuration.ADMIN_PASSWORD_HASH, 
                    new User("", "root", 0).getHashedPassword());
        this.setProperty(Configuration.ADMIN_USER_LEVEL, "1000");
        
        this.setProperty(Configuration.AUTO_LOGIN, "on");
        this.setProperty(Configuration.AUTO_LOGIN_TIME, "45000");
        this.setProperty(Configuration.IDENT, "");
        this.setProperty(Configuration.NICKNAME, "polly");
        this.setProperty(Configuration.RECONNECT_DELAY, "10000");
        this.setProperty(Configuration.SERVER, "irc.euirc.net");
        this.setProperty(Configuration.LINE_LENGTH, "201");
        this.setProperty(Configuration.MESSAGE_DELAY, "251");
        this.setProperty(Configuration.PORT, "6669");
        
        this.setProperty(Configuration.DATE_FORMAT, "dd.MM.yyyy HH\\:mm\\:ss");
        this.setProperty(Configuration.NUMBER_FORMAT, "0.\\#\\#\\#\\#\\#");
        
        
        this.setProperty(Configuration.DB_DRIVER, "org.hsqldb.jdbcDriver");
        this.setProperty(Configuration.DB_PASSWORD, "polly123");
        this.setProperty(Configuration.DB_URL, "jdbc\\:hsqldb\\:file\\:./db/polly");
        this.setProperty(Configuration.DB_USER, "polly");
        
        this.setProperty(Configuration.ENABLE_TELNET, "on");
        this.setProperty(Configuration.TELNET_PORT, "23");
        this.setProperty(Configuration.EVENT_THREADS, "4");
        this.setProperty(Configuration.EXECUTION_THREADS, "4");
        this.setProperty(Configuration.AUTO_UPDATE, "off");
        this.setProperty(Configuration.ENCODING, "ISO-8859-1");
        
        this.setProperty(Configuration.LOG_CONFIG_FILE, "cfg/log4j.cfg");
        this.setProperty(Configuration.DECLARATION_CACHE, "./cache");
    }

}
