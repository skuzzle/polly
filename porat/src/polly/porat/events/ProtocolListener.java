package polly.porat.events;

import java.util.EventListener;


public interface ProtocolListener extends EventListener{

    public abstract void responseReceived(ProtocolEvent e);
    
    public abstract void errorReceived(ProtocolEvent e);
        
}
