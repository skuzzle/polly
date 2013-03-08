package polly.rx.core;

import java.util.List;

import polly.rx.entities.TrainEntityV2;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;


/**
 * 
 * @author Simon
 * @version 27.07.2011 3851c1b
 */
public class TrainManagerV2 extends AbstractDisposable {

    private PersistenceManager persistence;
    private UserManager userManager;
    
    
    
    public TrainManagerV2(MyPolly myPolly) {
        this.persistence = myPolly.persistence();
        this.userManager = myPolly.users();
    }
    
    
    
    public User getTrainer(int id) {
        return this.userManager.getUser(id);
    }
    
    
    public User getTrainer(String name) {
        return this.userManager.getUser(name);
    }
    
    
    
    public synchronized TrainBillV2 getClosedTrains(String forUser) {
        return new TrainBillV2(this.persistence.atomicRetrieveList(TrainEntityV2.class, 
                TrainEntityV2.CLOSED_BY_USER, forUser));
    }
    
    
    
    public synchronized TrainBillV2 getAllOpenTrains(String forUser) {
        return new TrainBillV2(this.persistence.atomicRetrieveList(TrainEntityV2.class, 
                TrainEntityV2.OPEN_BY_USER, forUser));
    }
    
    
    
    public synchronized TrainBillV2 getBill(User trainer, String forUser) {
        List<TrainEntityV2> trains = this.persistence.atomicRetrieveList(
            TrainEntityV2.class, TrainEntityV2.OPEN_BY_USER_AND_TRAINER, 
            trainer.getId(), forUser);
        
        return new TrainBillV2(trains);
    }
    
    
    
    public synchronized TrainBillV2 getOpenTrains(User trainer) {
        return new TrainBillV2(this.persistence.atomicRetrieveList(
                TrainEntityV2.class, TrainEntityV2.OPEN_BY_TRAINER, trainer.getId()));
        
    }
    
    
    public synchronized TrainBillV2 getClosedTrains(User trainer) {
        return new TrainBillV2(this.persistence.atomicRetrieveList(
                TrainEntityV2.class, TrainEntityV2.CLOSED_BY_TRAINER, trainer.getId()));
        
    }
    
    
    
    public void closeOpenTrains(final TrainBillV2 bill) throws DatabaseException {
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                bill.closeBill();
            }
        });
    }
    
    
    
    public void closeOpenTrains(User trainer, String forUser) throws DatabaseException {
        this.closeOpenTrains(this.getBill(trainer, forUser));
    }
    
    
    
    public void closeOpenTrain(User trainer, final int id) 
                throws DatabaseException, CommandException {
        final TrainEntityV2 te = this.persistence.atomicRetrieveSingle(
                TrainEntityV2.class, id);

        if (te == null) {
            throw new CommandException("Ungültige Train-Id");
        } else if (te.getTrainerId() != trainer.getId()) {
            throw new CommandException("Ungültige Trainer Id");
        }
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                te.setClosed(true);
            }
        });
    }
    
    
    
    public synchronized TrainBillV2 addTrain(TrainEntityV2 e, User trainer) throws DatabaseException {
        this.persistence.atomicPersist(e);
        return this.getBill(trainer, e.getForUser());
    }
    
    
    
    public void deleteTrain(final int id) throws DatabaseException, CommandException {
        final TrainEntityV2 te = this.persistence.atomicRetrieveSingle(
            TrainEntityV2.class, id);

        if (te == null) {
            throw new CommandException("Ungültiger Train-Id");
        }
        persistence.atomicRemove(te);
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        // nothing to do here
    }
}