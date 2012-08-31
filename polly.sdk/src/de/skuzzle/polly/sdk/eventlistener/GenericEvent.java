package de.skuzzle.polly.sdk.eventlistener;


public class GenericEvent {

    private Object source;
    private String type;
    private Object data;
    
    
    
    public GenericEvent(Object source, String type, Object data) {
        super();
        this.source = source;
        this.type = type;
        this.data = data;
    }
    
    
    
    
    public Object getData() {
        return this.data;
    }
    
    
    
    
    public Object getSource() {
        return this.source;
    }
    
    
    
    public String getType() {
        return this.type;
    }
}