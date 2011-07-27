package de.skuzzle.polly.sdk;



/**
 * This class provides method to safely shutdown and restart polly.
 * 
 * @author Simon
 * @since Beta 0.5
 * @version 27.07.2011 0366137
 */
public interface ShutdownManager {

    /**
     * Performs a clean shutdown of polly.
     */
    public abstract void shutdown();
    
    
    
    /**
     * Performs a clean shutdown of polly and then restarts her using default 
     * configuration.
     */
    public abstract void restart();
    
    
    
    /**
     * Performs a clean shutdown of polly and then restarts her using given commandline
     * arguments.
     * @param args String that gets passed to polly when starting the new instance.
     */
    public abstract void restart(String args);
}