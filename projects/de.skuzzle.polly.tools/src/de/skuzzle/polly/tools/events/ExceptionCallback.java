package de.skuzzle.polly.tools.events;

/**
 * Interface for providing errors which occur during event dispatching to the caller.
 * 
 * @author Simon Taddiken
 */
public interface ExceptionCallback {

    /**
     * Callback method which gets passed an exception.
     * @param e The exception which occurred during event dispatching.
     */
    public void exception(Exception e);
}
