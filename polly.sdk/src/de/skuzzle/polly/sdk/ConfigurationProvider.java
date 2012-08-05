package de.skuzzle.polly.sdk;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.ConfigurationException;

import de.skuzzle.polly.sdk.eventlistener.ConfigurationListener;


/**
 * This class grants access to polly configuration files. It automatically locates 
 * config files within pollys config directories and thus gives the user easy access
 * to those files.
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface ConfigurationProvider {

    /**
     * <p>This method will open the configuration file with the given name. Once opened,
     * the configuration is stored in a cache to be easily accessible when trying to
     * reopen it within short time. The caching behavior is not specified.</p>
     * 
     * <p>When trying to open a configuration, polly first tries to look it up in its 
     * memory cache. If the config has not been opened before or was removed from the
     * cache, polly then searches the configuration directory and then the plugin 
     * configuration directory for the file. If it still can't be found, a 
     * {@link FileNotFoundException} willl be thrown.</p>
     * 
     * @param cfgName The name of the configuration to open.
     * @return The Configuration instance that was opened.
     * @throws FileNotFoundException If no configuration file with the given name could
     *          be located in any of the mentioned folders.
     * @throws IOException If reading the configuration fails.
     */
    public abstract Configuration open(String cfgName) 
            throws FileNotFoundException, IOException;
    
    
    
    /**
     * <p>This method will open the configuration file with the given name. Once opened,
     * the configuration is stored in a cache to be easily accessible when trying to
     * reopen it within short time. The caching behavior is not specified.</p>
     * 
     * <p>When trying to open a configuration, polly first tries to look it up in its 
     * memory cache. If the config has not been opened before or was removed from the
     * cache, polly then searches the configuration directory and then the plugin 
     * configuration directory for the file. If it still can't be found, a 
     * {@link FileNotFoundException} willl be thrown.</p>
     * 
     * <p>This method performs additional validation of the configuration using the given
     * validator.</p>
     * 
     * @param cfgName The name of the configuration to open.
     * @param validator The validator to ensure this configs validity.
     * @return The Configuration instance that was opened.
     * @throws FileNotFoundException If no configuration file with the given name could
     *          be located in any of the mentioned folders.
     * @throws IOException If reading the configuration fails.
     * @throws ConfigurationException If the config could be loaded, but fails during 
     *          validation.
     */
    public abstract Configuration open(String cfgName, ConfigurationValidator validator) 
            throws FileNotFoundException, IOException, ConfigurationException;
    
    
    
    /**
     * Returns a new, totally empty {@link Configuration} instance.
     * 
     * @return An empty configuration.
     */
    public abstract Configuration emptyConfiguration();
    
    
    
    /**
     * Returns the instance of pollys main configuration file.
     * 
     * @return A configuration instance for pollys main config file.
     */
    public abstract Configuration getRootConfiguration();
    
    
    
    /**
     * Registers the given listener to listen for configuration changes.
     *  
     * @param listener The listener to register
     * @since 0.8
     */
    public abstract void addConfigurationListener(ConfigurationListener listener);
    
    
    
    /**
     * Removes the given listener.
     * 
     * @param listener The listener to remove
     * @since 0.8
     */
    public abstract void removeConfigurationListener(ConfigurationListener listener);
}
