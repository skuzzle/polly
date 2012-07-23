package de.skuzzle.polly.sdk;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface ConfigurationProvider {

    /**
     * This method will open the configuration file with the given name. 
     * @param cfgName
     * @return
     */
    public abstract Configuration open(String cfgName) 
            throws FileNotFoundException, IOException;
    
    
    public abstract Configuration emptyConfiguration();
    
    
    
    public abstract Configuration getRootConfiguration();
}
