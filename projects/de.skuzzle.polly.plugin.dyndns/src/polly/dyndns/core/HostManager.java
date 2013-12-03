package polly.dyndns.core;

import java.util.List;

import polly.dyndns.entities.Hoster;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class HostManager {

    private final PersistenceManagerV2 persistence;
    
    
    
    public HostManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    
    
    
    public void addHoster(Hoster hoster) throws DatabaseException {
        try (Write w = this.persistence.write()) {
            w.single(hoster);
        }
    }
    
    
    
    public List<Hoster> getAllHosters() {
        return this.persistence.atomic().findList(Hoster.class, Hoster.QUERY_ALL_HOSTERS);
    }
    
    
    
    public void removeHoster(int hosterId) throws DatabaseException {
        try (Write w = this.persistence.write()) {
            final Read read = w.read();
            
            final Hoster hoster = read.find(Hoster.class, hosterId);
            if (hoster == null) {
                return;
            }
            w.remove(hoster);
        }
    }
}
