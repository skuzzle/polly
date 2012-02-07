package polly.network.protocol;

import polly.network.protocol.Constants.ResponseType;


public class Response extends ProtocolObject {

    private static final long serialVersionUID = 1L;

    private int responseFor;
    private ResponseType type;
    
    
    
    public Response(Request request, ResponseType type) {
        this(type);
        this.responseFor = request.getId();
    }
    
    
    
    public Response(ResponseType type) {
        this.type = type;
        this.responseFor = -1;
    }
    
    
    
    public int getResponseFor() {
        return this.responseFor;
    }
    
    
    
    public ResponseType getType() {
        return this.type;
    }
    
    
    
    public boolean is(ResponseType type) {
        return this.type == type;
    }
    
    
    
    @Override
    public String toString() {
        return super.toString() + ": " + this.type + "\n" + this.getPayload();
    }
}
