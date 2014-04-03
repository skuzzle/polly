package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;


public interface GenericListener extends EventListener {
    
    public abstract void genericEvent(GenericEvent e);
}
