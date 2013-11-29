package de.skuzzle.polly.sdk.eventlistener;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


public interface GenericListener extends EventListener {
    
    public final static Dispatch<GenericListener, GenericEvent> GENERIC_EVENT = 
            new Dispatch<GenericListener, GenericEvent>() {
        @Override
        public void dispatch(GenericListener listener, GenericEvent event) {
            listener.genericEvent(event);
        }
    };
    
    public abstract void genericEvent(GenericEvent e);
}
