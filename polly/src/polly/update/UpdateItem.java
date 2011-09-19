package polly.update;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import polly.core.PluginConfiguration;

import de.skuzzle.polly.sdk.Version;


public class UpdateItem {

    private String name;
    private Version currentVersion;
    private URL propertyUrl;
    
    
    public static UpdateItem fromProperties(Properties props) 
                throws MalformedURLException {
        String name = props.getProperty(PluginConfiguration.PLUGIN_NAME);
        String url = props.getProperty(PluginConfiguration.UPDATE_URL);
        String version = props.getProperty(PluginConfiguration.PLUGIN_VERSION);
        
        return new UpdateItem(name, new Version(version), new URL(url));
    }
    
    
    
    public UpdateItem(String name, Version currentVersion, URL propertyUrl) {
        this.name = name;
        this.currentVersion = currentVersion;
        this.propertyUrl = propertyUrl;
    }



    
    public Version getCurrentVersion() {
        return currentVersion;
    }



    
    public URL getPropertyUrl() {
        return propertyUrl;
    }
    
    
    
    @Override
    public String toString() {
        return this.name;
    }
}
