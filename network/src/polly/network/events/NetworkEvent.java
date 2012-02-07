package polly.network.events;

import polly.network.Connection;


public class NetworkEvent {
    
    private Connection source;
    
    public NetworkEvent(Connection source) {
        this.source = source;
    }
    
    
    
    public Connection getSource() {
        return this.source;
    }
}