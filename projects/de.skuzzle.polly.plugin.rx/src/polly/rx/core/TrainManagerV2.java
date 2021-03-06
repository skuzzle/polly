package polly.rx.core;

import java.util.List;

import polly.rx.MSG;
import polly.rx.core.TrainingEvent.TrainEventType;
import polly.rx.entities.TrainEntityV3;
import de.skuzzle.jeve.EventProvider;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.tools.concurrent.RunLater;

/**
 * 
 * @author Simon
 * @version 27.07.2011 3851c1b
 */
public class TrainManagerV2 extends AbstractDisposable {

    private PersistenceManagerV2 persistence;
    private UserManager userManager;
    private final EventProvider events;
    
    
    
    public TrainManagerV2(MyPolly myPolly) {
        this.persistence = myPolly.persistence();
        this.userManager = myPolly.users();
        events = EventProvider.newDefaultEventProvider();
    }
    
    
    
    public void addTrainListener(TrainingListener listener) {
        this.events.addListener(TrainingListener.class, listener);
    }
    
    
    
    public void removeTrainingListener(TrainingListener listener) {
        this.events.removeListener(TrainingListener.class, listener);
    }
    
    
    
    public User getTrainer(int id) {
        return this.userManager.getUser(id);
    }
    
    
    public User getTrainer(String name) {
        return this.userManager.getUser(name);
    }
    
    
    
    public synchronized TrainBillV2 getClosedTrains(String forUser) {
        return new TrainBillV2(this.persistence.atomic().findList(TrainEntityV3.class, 
                TrainEntityV3.CLOSED_BY_USER, new Param(forUser)));
    }
    
    
    
    public synchronized TrainBillV2 getAllOpenTrains(String forUser) {
        return new TrainBillV2(this.persistence.atomic().findList(TrainEntityV3.class, 
                TrainEntityV3.OPEN_BY_USER, new Param(forUser)));
    }
    
    
    
    public synchronized TrainBillV2 getBill(User trainer, String forUser) {
        List<TrainEntityV3> trains = this.persistence.atomic().findList(
            TrainEntityV3.class, TrainEntityV3.OPEN_BY_USER_AND_TRAINER, 
            new Param(trainer.getId(), forUser));
        
        return new TrainBillV2(trains);
    }
    
    
    
    public synchronized TrainBillV2 getOpenTrains(User trainer) {
        return new TrainBillV2(this.persistence.atomic().findList(
                TrainEntityV3.class, TrainEntityV3.OPEN_BY_TRAINER, 
                new Param(trainer.getId())));
        
    }
    
    
    public synchronized TrainBillV2 getClosedTrains(User trainer) {
        return new TrainBillV2(this.persistence.atomic().findList(
                TrainEntityV3.class, TrainEntityV3.CLOSED_BY_TRAINER, 
                new Param(trainer.getId())));
        
    }
    
    
    
    public void closeOpenTrains(final TrainBillV2 bill) throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                bill.closeBill();
                events.dispatch(TrainingListener.class, 
                        new TrainingEvent(TrainManagerV2.this, bill), 
                        TrainingListener::billClosed);
            }
        });
    }
    
    
    
    public void closeOpenTrains(User trainer, String forUser) throws DatabaseException {
        this.closeOpenTrains(this.getBill(trainer, forUser));
    }
    
    
    
    public void closeOpenTrain(User trainer, final int id) 
                throws DatabaseException, CommandException {
        final TrainEntityV3 te = this.persistence.atomic().find(
                TrainEntityV3.class, id);

        if (te == null) {
            throw new CommandException(MSG.trainManagerInvalidTrainId);
        } else if (te.getTrainerId() != trainer.getId()) {
            throw new CommandException(MSG.trainManagerInvalidTrainerId);
        }
        
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                te.setClosed(true);
            }
        });
    }
    
    
    
    public synchronized TrainBillV2 addTrain(final TrainEntityV3 e, User trainer) 
            throws DatabaseException {
        events.dispatch(TrainingListener.class, 
                new TrainingEvent(TrainManagerV2.this, TrainEventType.TRAIN_ADDED, e), 
                TrainingListener::trainingAdded);
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) throws DatabaseException {
                write.single(e);
                new RunLater("TRAIN_EVENT", e.getTrainFinished()) { //$NON-NLS-1$
                    @Override
                    public void run() {
                        events.dispatch(TrainingListener.class, 
                                new TrainingEvent(TrainManagerV2.this, TrainEventType.TRAIN_FINISHED, e), 
                                TrainingListener::trainingFinished);
                    }
                }.start();
            }
        });
        return this.getBill(trainer, e.getForUser());
    }
    
    
    
    public void deleteTrain(final int id) throws DatabaseException, CommandException {
        final TrainEntityV3 te = this.persistence.atomic().find(TrainEntityV3.class, id);

        if (te == null) {
            throw new CommandException(MSG.trainManagerInvalidTrainId);
        }
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) throws DatabaseException {
                write.remove(te);
            }
        });
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        // nothing to do here
    }
}