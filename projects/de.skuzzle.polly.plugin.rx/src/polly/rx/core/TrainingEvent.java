package polly.rx.core;

import polly.rx.entities.TrainEntityV3;
import de.skuzzle.polly.tools.events.Event;

public class TrainingEvent extends Event<TrainManagerV2> {

    public static enum TrainEventType {
        TRAIN_ADDED, TRAIN_FINISHED, TRAIN_CLOSED, BILL_CLOSED;
    }

    private final TrainEventType eventType;
    private final TrainEntityV3 training;
    private final TrainBillV2 bill;


    public TrainingEvent(TrainManagerV2 source, TrainEventType tet, TrainEntityV3 te) {
        super(source);
        this.eventType = tet;
        this.training = te;
        this.bill = null;
    }
    
    
    public TrainingEvent(TrainManagerV2 source, TrainBillV2 bill) {
        super(source);
        this.eventType = TrainEventType.BILL_CLOSED;
        this.training = null;
        this.bill = bill;
    }
    
    
    
    public TrainBillV2 getBill() {
        return this.bill;
    }



    public TrainEventType getEventType() {
        return this.eventType;
    }



    public TrainEntityV3 getTraining() {
        return this.training;
    }
}
