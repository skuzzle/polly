package polly.rx.core;

import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import polly.rx.entities.ScoreBoardEntry;


public class ScoreBoardManager {

    private PersistenceManager persistence;
    
    
    
    public ScoreBoardManager(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public void addEntry(final ScoreBoardEntry entry) throws DatabaseException {
        
        Collection<ScoreBoardEntry> existing = this.getEntries(entry.getVenadName());
        
        // skip identical entries.
        for (ScoreBoardEntry e : existing) {
            if (e.getPoints() == entry.getPoints() && e.getRank() == entry.getRank()) {
                return;
            }
        }
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.persist(entry);
            }
        });
    }
    
    
    
    public void deleteEntry(int id) throws DatabaseException {
        ScoreBoardEntry sbe = this.persistence.atomicRetrieveSingle(
            ScoreBoardEntry.class, id);
        
        if (sbe == null) { 
            return;
        }
        
        this.persistence.atomicRemove(sbe);
    }
    
    
    
    
    public Collection<ScoreBoardEntry> getEntries(String venad) {
        return this.persistence.atomicRetrieveList(ScoreBoardEntry.class, 
            ScoreBoardEntry.SBE_BY_USER, venad);
    }
    
    
    
    public List<ScoreBoardEntry> getEntries() {
        return this.persistence.atomicRetrieveList(ScoreBoardEntry.class, 
            ScoreBoardEntry.ALL_SBE_DISTINCT);
    }
    
}
