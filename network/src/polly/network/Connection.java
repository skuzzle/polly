package polly.network;

import polly.network.protocol.ProtocolObject;


public interface Connection {

    public abstract void send(ProtocolObject item);
    
    public abstract boolean isConnected();
    
    public abstract boolean isAuthenticated();
}