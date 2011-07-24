package de.skuzzle.polly.sdk;

import java.io.IOException;

/**
 * <p>Provides access to the polly configuration file.</p>
 * 
 * <p>You can add your own property values and store them in pollys configuration file or
 * access existing properties from that file.</p>
 * 
 * <p>You should never override existing values which you did not set yourself!</p>
 * 
 * <p>Please note that you have to call {@link #store()} in order to persist all of your 
 * changes. Polly automatically persists the config file upon shutdown</p>
 * 
 * <p>Please note also, that other plugins may change the properties in this file at any 
 * time. The values you get are only considerable correct by the time you 
 * received them.</p>
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface Configuration {

	/**
	 * <p>Stores a property with given name and value. If a property with the same name
	 * existed before, it will be overridden. If it not existed, it will be created.</p>
	 * 
	 * <p>Note that you need to call {@link #store()} in order to persist these 
	 * changes.</p>
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
	 * Writes the current properties back to the configuration file.
	 * @throws IOException If writing the file fails.
	 */
	public abstract void store() throws IOException;
}