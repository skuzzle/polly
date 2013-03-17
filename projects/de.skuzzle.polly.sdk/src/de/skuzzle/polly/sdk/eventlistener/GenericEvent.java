package de.skuzzle.polly.sdk.eventlistener;

import de.skuzzle.polly.tools.events.Event;


public class GenericEvent extends Event<Object> {

    private String type;
    private Object data;
    
    
    
    public GenericEvent(Object source, String type, Object data) {
        super(source);
        this.type = type;
        this.data = data;
    }
    
    
    
    
    public Object getData() {
        return this.data;
    }
    
    
    
    public String getType() {
        return this.type;
    }
}