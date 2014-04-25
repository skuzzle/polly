package polly.mud.connection;

import de.skuzzle.jeve.Listener;


public interface ConnectionListener extends Listener {
    
    public void received(MudMessageEvent e);



    public void connected(MudEvent e);



    public void disconnected(MudEvent e);
}
