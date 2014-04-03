package polly.rx.core;

import java.util.EventListener;


public interface TrainingListener extends EventListener {
    public void trainingAdded(TrainingEvent e);
    
    public void trainingFinished(TrainingEvent e);
    
    public void trainingClosed(TrainingEvent e);
    
    public void billClosed(TrainingEvent e);
}
