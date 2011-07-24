package de.skuzzle.polly.sdk;

/**
 * Disposable objects are used to release resources upon unloading of your plugin. If
 * you have classes in your plugin which need to free resources if you plugin unloads,
 * make them implement this class and add them to your 'MyPlugin' class using 
 * {@link PollyPlugin#addDisposable(Disposable)}.
 * 
 * @author Simon
 * @since zero day
 * @version RC 1.0
 */
public interface Disposable {
    
    /**
     * Call this method to free all resources of the implementing class.
     */
    public abstract void dispose();
}