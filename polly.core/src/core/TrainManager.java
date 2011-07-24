package core;

import java.util.List;

import de.skuzzle.polly.sdk.Disposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.TrainEntity;



public class TrainManager implements Disposable {

    private PersistenceManager persistence;
    
    
    public TrainManager(MyPolly myPolly) {
        this.persistence = myPolly.persistence();
    }
    
    
    
    public TrainBill getBill(String forUser) {
        try {
            this.persistence.readLock();
            List<TrainEntity> trains = this.persistence.findList(TrainEntity.class, 
                "OPEN_BY_USER", forUser);
            return new TrainBill(trains);
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public void closeOpenTrains(TrainBill bill) throws DatabaseException {
        try {
            this.persistence.writeLock();
            this.persistence.startTransaction();
            bill.close();
            this.persistence.commitTransaction();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    public void closeOpenTrains(String forUser) throws DatabaseException {
        this.closeOpenTrains(this.getBill(forUser));
    }
    
    
    public void closeOpenTrain(int id) throws DatabaseException {
        try {
            this.persistence.writeLock();
            TrainEntity e = this.persistence.find(TrainEntity.class, id);
            if (e == null) {
                throw new IllegalArgumentException("No item with id " + id);
            }
            this.persistence.startTransaction();
            e.setOpen(false);
            this.persistence.commitTransaction();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    public void addTrain(TrainEntity e) throws DatabaseException {
        try {
            this.persistence.writeLock();
            this.persistence.startTransaction();
            this.persistence.persist(e);
            this.persistence.commitTransaction();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    public void deleteTrain(int id) throws DatabaseException {
        try {
            this.persistence.writeLock();
            TrainEntity e = this.persistence.find(TrainEntity.class, id);
            
            if (e == null) {
                throw new IllegalArgumentException("No item with id " + id);
            }
            this.persistence.startTransaction();
            this.persistence.remove(e);
            this.persistence.commitTransaction();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }
}