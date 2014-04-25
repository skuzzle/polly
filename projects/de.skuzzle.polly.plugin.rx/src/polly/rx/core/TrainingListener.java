package polly.rx.core;

import de.skuzzle.jeve.Listener;


public interface TrainingListener extends Listener {
    public void trainingAdded(TrainingEvent e);
    
    public void trainingFinished(TrainingEvent e);
    
    public void trainingClosed(TrainingEvent e);
    
    public void billClosed(TrainingEvent e);
}
