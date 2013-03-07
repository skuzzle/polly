package de.skuzzle.polly.sdk.http;

import java.util.EventListener;

/**
 * This listener listens for http actions and can be used with 
 * {@link HttpManager#addHttpEventListener(HttpEventListener)}. It will get notified each
 * time a user accesses a web page that matches the return value of 
 * {@link #getActionUrl()}.
 * 
 * @author Simon
 * @since 0.9.1
 */
public interface HttpEventListener extends EventListener {
    
    /**
     * Provides a pattern string to determine for which urls this listener gets 
     * notified.
     * 
     * @return A suitable regular expression pattern for uri matching.
     */
    public abstract String getActionUrl();
    
    
    
    /**
     * <p>This method gets called by polly if this listener was registered with the 
     * {@link HttpManager} and a user accessed an uri that matched the return value of
     * {@link #getActionUrl()}.</p>
     * 
     * <p>The passed {@link HttpEvent} contains detailed information about the event 
     * such as the current session of the executing user-</p>
     * 
     * @param e 
     */
    public abstract void httpAction(HttpEvent e);
}