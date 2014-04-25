package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.jeve.Listener;


public interface GenericListener extends Listener {
    
    public abstract void genericEvent(GenericEvent e);
}
