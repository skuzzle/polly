package polly.annoyingPeople.core;

import de.skuzzle.jeve.Listener;


public interface PersonListener extends Listener {

    public void personAdded(AnnoyingPersonEvent e);
    
    public void personRemoved(AnnoyingPersonEvent e);
}