package polly.rx.core;

import java.util.List;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.entities.AZEntry;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class AZEntryManager {
    
    private final PersistenceManagerV2 persistence;
    private final MyPolly myPolly;
    
    
    
    public AZEntryManager(MyPolly myPolly) {
        this.myPolly = myPolly;
        this.persistence = myPolly.persistence();
    }
    
    
    
    public List<AZEntry> getEntries(int byUser) {
        return this.persistence.atomic().findList(AZEntry.class, 
                AZEntry.AZ_ENTRY_BY_USER, new Param(byUser));
    }
    
    
    
    public void deleteEntry(int id, int byUserId) throws DatabaseException {
        try (final Write w = this.persistence.write()) {
            final AZEntry e = w.read().find(AZEntry.class, id);
            
            if (e != null) {
                if (e.getByUserId() != byUserId) {
                    throw new DatabaseException(MSG.azEntryCantDeleteOther);
                }
                w.remove(e);
            }
        }
    }
    
    
    
    public void addEntry(int byUserId, String fleet, String az) throws DatabaseException {
        try (final Write w = this.persistence.write()) {
            final AZEntry check = w.read().findSingle(AZEntry.class, 
                    AZEntry.AZ_ENTRY_BY_FLEET_AND_USER, new Param(fleet, byUserId));
            
            if (check != null) {
                // user already has an entry for this fleet. remove and replace
                w.remove(check);
            }
            final AZEntry newEntry = new AZEntry(byUserId, fleet, az);
            w.single(newEntry);
        }
    }
    
    
    
    public TimespanType getAz(String fleet, User executor) {
        try (final Read r = this.persistence.read()) {
            final AZEntry e = r.findSingle(AZEntry.class, 
                    AZEntry.AZ_ENTRY_BY_FLEET_AND_USER, 
                    new Param(fleet, executor.getId()));

            if (e == null) {
                return (TimespanType) executor.getAttribute(MyPlugin.AUTO_REMIND_AZ);
            } else {
                return (TimespanType) this.myPolly.parse(e.getAz());
            }
        }
    }
}
