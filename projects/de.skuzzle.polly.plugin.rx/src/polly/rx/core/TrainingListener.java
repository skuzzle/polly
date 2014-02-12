package polly.rx.core;

import java.util.EventListener;

import de.skuzzle.polly.tools.events.Dispatch;


public interface TrainingListener extends EventListener {
    
    public final static Dispatch<TrainingListener, TrainingEvent> TRAINING_ADDED = 
            new Dispatch<TrainingListener, TrainingEvent>() {
        @Override
        public void dispatch(TrainingListener listener, TrainingEvent event) {
            listener.trainingAdded(event);
        }
    };
    
    public final static Dispatch<TrainingListener, TrainingEvent> TRAINING_FINISHED = 
            new Dispatch<TrainingListener, TrainingEvent>() {
        @Override
        public void dispatch(TrainingListener listener, TrainingEvent event) {
            listener.trainingFinished(event);
        }
    };
    
    public final static Dispatch<TrainingListener, TrainingEvent> TRAINING_CLOSED = 
            new Dispatch<TrainingListener, TrainingEvent>() {
        @Override
        public void dispatch(TrainingListener listener, TrainingEvent event) {
            listener.trainingClosed(event);
        }
    };
    
    public final static Dispatch<TrainingListener, TrainingEvent> BILL_CLOSED = 
            new Dispatch<TrainingListener, TrainingEvent>() {
        @Override
        public void dispatch(TrainingListener listener, TrainingEvent event) {
            listener.billClosed(event);
        }
    };

    
    
    public void trainingAdded(TrainingEvent e);
    
    public void trainingFinished(TrainingEvent e);
    
    public void trainingClosed(TrainingEvent e);
    
    public void billClosed(TrainingEvent e);
}
