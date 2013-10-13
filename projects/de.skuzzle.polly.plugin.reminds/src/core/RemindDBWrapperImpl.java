package core;

import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.RemindEntity;


public class RemindDBWrapperImpl implements RemindDBWrapper {

    private PersistenceManagerV2 persistence;
    
    
    public RemindDBWrapperImpl(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }

    

    @Override
    public void deleteRemind(final RemindEntity remind) throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            
            @Override
            public void perform(Write write) {
                write.remove(remind);
            }
        });
    }


    
    @Override
    public void addRemind(final RemindEntity remind) throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            
            @Override
            public void perform(Write write) {
                write.single(remind);
            }
        });
    }


    
    @Override
    public RemindEntity getRemind(int id) {
        return this.persistence.atomic().find(RemindEntity.class, id);
    }


    @Override
    public List<RemindEntity> getAllReminds() {
        return this.persistence.atomic().findList(RemindEntity.class, 
                RemindEntity.ALL_REMINDS);
    }

    

    @Override
    public List<RemindEntity> getRemindsForUser(String forUser) {
        return this.persistence.atomic().findList(
                RemindEntity.class, RemindEntity.REMIND_FOR_USER, new Param(forUser));
    }


    
    @Override
    public List<RemindEntity> getMyRemindsForUser(String nickName) {
        return this.persistence.atomic().findList(
                RemindEntity.class, RemindEntity.MY_REMIND_FOR_USER, new Param(nickName));
    }


    
    @Override
    public List<RemindEntity> getUndeliveredReminds(String forUser) {
        return this.persistence.atomic().findList(RemindEntity.class, 
                RemindEntity.UNDELIVERED_FOR_USER, 
                new Param(forUser));
    }
}
