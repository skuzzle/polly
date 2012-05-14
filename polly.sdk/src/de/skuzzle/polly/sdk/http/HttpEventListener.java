package de.skuzzle.polly.sdk.http;

import java.util.EventListener;


public interface HttpEventListener extends EventListener {
    
    public abstract String getActionUrl();
    
    public abstract void httpAction(HttpEvent e);
}