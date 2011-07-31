package polly.update;

import java.net.URL;

import de.skuzzle.polly.sdk.Version;


public class UpdateItem {

    private String name;
    private Version currentVersion;
    private URL propertyUrl;
    
    
    
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
