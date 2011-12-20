package polly.core.plugins;

import java.io.IOException;

import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

import polly.configuration.Configuration;
import polly.configuration.ConfigurationFileException;


/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class PluginConfiguration extends Configuration {
    
    public final static String JAR_FILE = "jarfile";
    public final static String PLUGIN_NAME = "name";
    public final static String PLUGIN_DESCRIPTION = "description";
    public final static String PLUGIN_DEVELOPER = "developer";
    public final static String PLUGIN_VERSION = "version";
    public final static String ENTRY_POINT = "entrypoint";
    public final static String UPDATE_URL = "updateUrl";
    

    private PollyPlugin pluginInstance;
    
    
    public PluginConfiguration(String filename)
            throws IOException, ConfigurationFileException {
        super(filename);
        this.init();
    }
    
    
    
    public PollyPlugin getPluginInstance() {
        return this.pluginInstance;
    }
    
    
    
    public void setPluginInstance(PollyPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    
    
    @Override
    protected void init() throws ConfigurationFileException {
        if (this.props.getProperty(JAR_FILE) == null) {
            throw new ConfigurationFileException("Property missing: '" + JAR_FILE + "'");
        }
        
        if (this.props.getProperty(PLUGIN_NAME) == null) {
            throw new ConfigurationFileException(
                    "Property missing: '" + PLUGIN_NAME + "'");
        }
        
        if (this.props.getProperty(ENTRY_POINT) == null) {
            throw new ConfigurationFileException(
                    "Property missing: '" + ENTRY_POINT + "'");
        }
        
        if (this.props.getProperty(UPDATE_URL) != null && 
            this.props.getProperty(PLUGIN_VERSION) == null) {
            throw new ConfigurationFileException("Property missing: '" + PLUGIN_VERSION +
                "'. If '" + UPDATE_URL + "' is specified, '" + PLUGIN_VERSION + "' is " +
                "required.");
        }
    }
    
    
    
    public boolean isUpdateable() {
        return this.props.getProperty(UPDATE_URL) != null;
    }
    
    
    
    public String getProperty(String name) {
        return this.props.getProperty(name, "");
    }
    
    
    public boolean updateSupported() {
        return this.props.getProperty(UPDATE_URL) != null;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("    ");
        b.append("Name: " + this.props.getProperty(PLUGIN_NAME) + "\n");
        b.append("    ");
        b.append("Developer: " + this.props.getProperty(PLUGIN_DEVELOPER, "") + "\n");
        b.append("    ");
        b.append("Description: " + this.props.getProperty(PLUGIN_DESCRIPTION, "") + "\n");
        b.append("    ");
        b.append("Version: " + this.props.getProperty(PLUGIN_VERSION, ""));
        return b.toString();
    }



    @Override
    protected void actualDispose() throws DisposingException {
        // do nothing
    }
}
