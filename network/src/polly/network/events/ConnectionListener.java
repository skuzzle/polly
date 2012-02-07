package polly.network.events;

import java.util.EventListener;


public interface ConnectionListener extends EventListener {

    public abstract void connectionAccepted(NetworkEvent e);
    
    public abstract void connectionClosed(NetworkEvent e);
}
