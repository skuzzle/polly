package polly.porat.events;

import java.util.EventListener;


public interface FilesReceivedListener extends EventListener {

    public abstract void filesReceived(FilesReceivedEvent e);
}