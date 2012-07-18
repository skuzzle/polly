package core;

import java.util.List;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import entities.TrainEntityV2;


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
    
    
    
    public int getTrainerId(String trainerNick) {
        return this.userManager.getUser(new IrcUser(trainerNick, "", "")).getId();
    }
    
    
    
    public TrainBillV2 getBill(int trainerId, String forUser) {
        List<TrainEntityV2> trains = this.persistence.atomicRetrieveList(
            TrainEntityV2.class, TrainEntityV2.OPEN_BY_USER_AND_TRAINER, 
            trainerId, forUser);
        
        return new TrainBillV2(trains);
    }
    
    
    
    public void closeOpenTrains(final TrainBillV2 bill) throws DatabaseException {
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                bill.closeBill();
            }
        });
    }
    
    
    
    public void closeOpenTrains(int trainerId, String forUser) throws DatabaseException {
        this.closeOpenTrains(this.getBill(trainerId, forUser));
    }
    
    
    
    public void closeOpenTrain(int trainerId, final int id) 
                throws DatabaseException, CommandException {
        final TrainEntityV2 te = this.persistence.atomicRetrieveSingle(
                TrainEntityV2.class, id);

        if (te == null) {
            throw new CommandException("Ungültiger Train-Id");
        } else if (te.getTrainerId() != trainerId) {
            throw new CommandException("Ungültige Trainer Id");
        }
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            
            @Override
            public void performUpdate(PersistenceManager persistence) {
                te.setClosed(true);
            }
        });
    }
    
    
    
    public void addTrain(TrainEntityV2 e) throws DatabaseException {
        this.persistence.atomicPersist(e);
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