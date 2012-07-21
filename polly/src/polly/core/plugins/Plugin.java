package polly.core.plugins;

import java.io.File;
import java.io.IOException;

import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.PluginException;

import polly.configuration.ConfigurationImpl;
import polly.util.PluginClassLoader;


/**
 * 
 * @author Simon
 * @version 27.07.2011 ae73250
 */
public class Plugin extends ConfigurationImpl implements Disposable {
    
    public final static String JAR_FILE = "jarfile";
    public final static String PLUGIN_NAME = "name";
    public final static String PLUGIN_DESCRIPTION = "description";
    public final static String PLUGIN_DEVELOPER = "developer";
    public final static String PLUGIN_VERSION = "version";
    public final static String ENTRY_POINT = "entrypoint";
    public final static String UPDATE_URL = "updateUrl";
    
    
    private boolean disposed;
    private PollyPlugin pluginInstance;
    private PluginClassLoader loader;
    
    
    public Plugin(String filename)
            throws IOException, PluginException {
        super(new File(filename));
        this.init();
    }
    
    
    
    public PollyPlugin getPluginInstance() {
        return this.pluginInstance;
    }
    
    
    
    public void setPluginInstance(PollyPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
    }
    
    
    
    
    public PluginClassLoader getLoader() {
        return this.loader;
    }
    

    
    public void setLoader(PluginClassLoader loader) {
        this.loader = loader;
    }

    
    
    protected void init() throws PluginException {
        if (this.readString(JAR_FILE) == null) {
            throw new PluginException("Property missing: '" + JAR_FILE + "'");
        }
        
        if (this.readString(PLUGIN_NAME) == null) {
            throw new PluginException(
                    "Property missing: '" + PLUGIN_NAME + "'");
        }
        
        if (this.readString(ENTRY_POINT) == null) {
            throw new PluginException(
                    "Property missing: '" + ENTRY_POINT + "'");
        }
        
        if (this.readString(UPDATE_URL) != null && 
            this.readString(PLUGIN_VERSION) == null) {
            throw new PluginException("Property missing: '" + PLUGIN_VERSION +
                "'. If '" + UPDATE_URL + "' is specified, '" + PLUGIN_VERSION + "' is " +
                "required.");
        }
    }
    
    
    
    public boolean isUpdateable() {
        return this.readString(UPDATE_URL) != null;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("    ");
        b.append("Name: " + this.readString(PLUGIN_NAME) + "\n");
        b.append("    ");
        b.append("Developer: " + this.readString(PLUGIN_DEVELOPER, "") + "\n");
        b.append("    ");
        b.append("Description: " + this.readString(PLUGIN_DESCRIPTION, "") + "\n");
        b.append("    ");
        b.append("Version: " + this.readString(PLUGIN_VERSION, ""));
        return b.toString();
    }

    
    
    @Override
    public synchronized boolean isDisposed() {
        return this.disposed;
    }
    
    

    @Override
    public synchronized void dispose() throws DisposingException {
        if (this.disposed) {
            throw new IllegalStateException("already disposed");
        }
        try {
            if (this.pluginInstance != null) {
                this.pluginInstance.dispose();
                this.pluginInstance = null;
            }
        } finally {
            this.disposed = true;
            this.loader.dispose();
        }
    }
}
