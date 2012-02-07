package polly.network.protocol;

import polly.network.protocol.Constants.RequestType;


public class Request extends ProtocolObject {
    
    private static final long serialVersionUID = 1L;

    private RequestType type;
    
    public Request(RequestType type) {
        this.type = type;
    }
    
    
    public RequestType getType() {
        return this.type;
    }
    
    
    public boolean is(RequestType type) {
        return this.type == type;
    }
    
    
    @Override
    public String toString() {
        return super.toString() + ": " + this.type + "\n" + this.getPayload();
    }
}
