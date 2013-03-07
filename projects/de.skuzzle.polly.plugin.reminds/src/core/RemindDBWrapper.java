package core;

import java.util.List;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;

import entities.RemindEntity;


public interface RemindDBWrapper {
    
    public abstract void deleteRemind(RemindEntity remind) throws DatabaseException;
    
    public abstract void addRemind(RemindEntity remind) throws DatabaseException;
    
    public abstract RemindEntity getRemind(int id);
    
    public abstract List<RemindEntity> getAllReminds();
    
    public abstract List<RemindEntity> getRemindsForUser(String forUser);
    
    public abstract List<RemindEntity> getMyRemindsForUser(String nickName);
    
    public abstract List<RemindEntity> getUndeliveredReminds(String forUser);
}