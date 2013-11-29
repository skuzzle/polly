package polly.mud.connection;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;

public interface ConnectionListener extends EventListener {

    public final static Dispatch<ConnectionListener, MudMessageEvent> RECEIVED = 
            new Dispatch<ConnectionListener, MudMessageEvent>() {

        @Override
        public void dispatch(ConnectionListener listener, MudMessageEvent event) {
            listener.received(event);
        }
    };

    
    
    public final static Dispatch<ConnectionListener, MudEvent> CONNECTED = 
            new Dispatch<ConnectionListener, MudEvent>() {

        @Override
        public void dispatch(ConnectionListener listener, MudEvent event) {
            listener.connected(event);
        }
    };
    
    
    
    public final static Dispatch<ConnectionListener, MudEvent> DISCONNECTED = 
            new Dispatch<ConnectionListener, MudEvent>() {

        @Override
        public void dispatch(ConnectionListener listener, MudEvent event) {
            listener.disconnected(event);
        }
    };

    
    public void received(MudMessageEvent e);



    public void connected(MudEvent e);



    public void disconnected(MudEvent e);
}
