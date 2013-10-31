package polly.mud.connection;

import java.util.EventListener;


public interface ConnectionListener extends EventListener {

    public void received(MudMessageEvent e);
    
    public void connected(MudEvent e);
    
    public void disconnected(MudEvent e);
}
