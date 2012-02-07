package polly.porat.events;

import polly.network.protocol.Constants.ResponseType;
import polly.network.protocol.Response;
import polly.porat.network.ClientProtocolHandler;


public class ProtocolEvent {

    private ClientProtocolHandler source;
    private Response response;
    
    
    
    public ProtocolEvent(ClientProtocolHandler source, Response response) {
        this.source = source;
        this.response = response;
    }
    
    
    
    public ClientProtocolHandler getSource() {
        return this.source;
    }
    
    
    
    
    public Response getResponse() {
        return this.response;
    }
    
    
    public ResponseType getType() {
        return this.response.getType();
    }
}
