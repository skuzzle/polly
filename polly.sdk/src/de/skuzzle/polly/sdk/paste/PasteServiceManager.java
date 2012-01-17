package de.skuzzle.polly.sdk.paste;

import de.skuzzle.polly.sdk.exceptions.PasteException;

/**
 * PasteServiceManager allows the usage of several HTTP internet pasteservices provided
 * by polly. Users can add their own service using {@link #addService(PasteService)}.
 * 
 * @author Simon
 * @since 0.7
 */
public interface PasteServiceManager {

	/**
	 * Adds a new {@link PasteService} to this manager. Its name must be unique and may
	 * not already exist.
	 * 
	 * @param service The service to add.
	 * @throws PasteException If a service with the same name already exists.
	 */
	public abstract void addService(PasteService service) throws PasteException;
	
	/**
	 * Gets a {@link PasteService} by its name. 
	 * 
	 * @param name The name of the paste service.
	 * @throws PasteException If no service with the given name exists.
	 */
	public abstract void getService(String name) throws PasteException;
	
	/**
	 * Uses a round robin method to determine the paste service which is returned. So
	 * each service will be used uniformly.
	 * 
	 * @return A "random" paste service.
	 */
	public abstract void getRandomService();
}