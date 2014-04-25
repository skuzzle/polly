package polly.dyndns.core;

import de.skuzzle.jeve.Listener;


public interface IPChangedListener extends Listener {

    public void ipChanged(IPChangedEvent e);
}
