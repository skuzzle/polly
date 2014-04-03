package polly.dyndns.core;

import java.util.EventListener;


public interface IPChangedListener extends EventListener {

    public void ipChanged(IPChangedEvent e);
}
