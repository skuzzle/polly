package polly.network.events;

import java.util.EventListener;


public interface ObjectReceivedListener extends EventListener {

    public abstract void objectReceived(ObjectReceivedEvent e);
}
