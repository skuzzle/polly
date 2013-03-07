package core;

import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.RemindEntity;


public class RemindDBWrapperImpl implements RemindDBWrapper {

    private PersistenceManager persistence;
    
    
    public RemindDBWrapperImpl(PersistenceManager persistence) {
        this.persistence = persistence;
    }

    

    @Override
    public void deleteRemind(RemindEntity remind) throws DatabaseException {
        this.persistence.atomicRemove(remind);
    }


    
    @Override
    public void addRemind(RemindEntity remind) throws DatabaseException {
        this.persistence.atomicPersist(remind);
    }


    
    @Override
    public RemindEntity getRemind(int id) {
        return this.persistence.atomicRetrieveSingle(RemindEntity.class, id);
    }


    @Override
    public List<RemindEntity> getAllReminds() {
        try {
            this.persistence.readLock();
            return this.persistence.findList(RemindEntity.class, "ALL_REMINDS");
        } finally {
            this.persistence.readUnlock();
        }
    }

    

    @Override
    public List<RemindEntity> getRemindsForUser(String forUser) {
        try {
            this.persistence.readLock();
            return this.persistence.findList(
                    RemindEntity.class, "REMIND_FOR_USER", forUser);
        } finally {
            this.persistence.readUnlock();
        }
    }


    
    @Override
    public List<RemindEntity> getMyRemindsForUser(String nickName) {
        try {
            this.persistence.readLock();
            return this.persistence.findList(
                    RemindEntity.class, "MY_REMIND_FOR_USER", nickName);
        } finally {
            this.persistence.readUnlock();
        }
    }


    
    @Override
    public List<RemindEntity> getUndeliveredReminds(String forUser) {
        try {
            this.persistence.readLock();
            return this.persistence.findList(RemindEntity.class, "UNDELIVERED_FOR_USER", 
                    forUser);
        } finally {
            this.persistence.readUnlock();
        }
    }
}
