package de.skuzzle.polly.sdk;

import java.io.File;

import de.skuzzle.polly.sdk.exceptions.PluginException;



/**
 * <p>Manages several plugin related tasks.</p> 
 * 
 * <p>Please mind that your own plugin should use this methods carefully in order not to 
 * deactivate other plugin or crash the whole polly.</p>
 *  
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface PluginManager {

	

	/**
	 * Instantly tries to load a plugin from the given file path. This method logs
	 * detailed error messages for the plugin initializing process.
	 * 
	 * @param propertyFile The filename of the plugins property file.
	 * @param myPoly The {@link MyPolly} instance that the new loaded plugin will work
	 * 		with.
	 * @throws PluginException If loading the plugin failed.
	 */
	public abstract void load(File propertyFile, MyPolly myPoly) 
	        throws PluginException;
	
	
	
	/**
	 * Unloads the plugin with the given name and frees all of its resources.
	 * If no plugin with the given name was loaded, nothing happens but a warning
	 * is written to the main log file.
	 * 
	 * @param pluginName The name of the plugin to unload. Note that this is the
	 *     real plugin name as defined by the property file and not
	 *     the jar filename.
	 * @throws PluginException If unloading the plugin failed or if the plugin has not
	 *     been loaded.
	 */
	public abstract void unload(String pluginName) throws PluginException;
	
	
	
	/**
	 * Tests whether a plugin with the given name is currently active.
	 * @param pluginName The plugin name to test. Note that this is the
     *     real plugin name as defined by your properties file and not
     *     the jar filename.
	 * @return <code>true</code> if a plugin with the given name is loaded. 
	 * 		<code>false</code> otherwise.
	 */
	public abstract boolean isLoaded(String pluginName);
}
