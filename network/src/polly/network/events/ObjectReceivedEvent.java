package polly.network.events;

import polly.network.Connection;
import polly.network.protocol.ProtocolObject;
import polly.network.protocol.Request;
import polly.network.protocol.Response;


public class ObjectReceivedEvent extends NetworkEvent {

    private ProtocolObject object;
    
    
    public ObjectReceivedEvent(Connection source, ProtocolObject object) {
        super(source);
        this.object = object;
    }
    
    
    
    public ProtocolObject getObject() {
        return this.object;
    }
    
    
    
    public boolean isRequest() {
        return this.object instanceof Request;
    }
    
    
    
    public boolean isResponse() {
        return this.object instanceof Response;
    }

}
