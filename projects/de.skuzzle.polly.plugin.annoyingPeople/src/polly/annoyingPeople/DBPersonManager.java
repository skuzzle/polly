package polly.annoyingPeople;

import polly.annoyingPeople.entities.AnnoyingPerson;
import de.skuzzle.jeve.EventProvider;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;

public class DBPersonManager implements PersonManager {

    private final PersistenceManagerV2 persistence;
    private final EventProvider eventProvider;

    

    public DBPersonManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
        this.eventProvider = EventProvider.newDefaultEventProvider();
    }

    
    
    @Override
    public AnnoyingPerson getAnnoyingPerson(String nickName, String channel) {
        return this.persistence.atomic().findSingle(AnnoyingPerson.class, 
                AnnoyingPerson.PERSON_BY_NAME_AND_CHANNEL, 
                new Param(nickName, channel));
    }

    

    @Override
    public synchronized AnnoyingPerson addAnnoyingPerson(String nickName, 
            String channel) throws DatabaseException {
        
        AnnoyingPerson added = null;
        try (final Write w = this.persistence.write()) {
            final Read read = w.read();
            
            final AnnoyingPerson check = read.findSingle(
                    AnnoyingPerson.class, AnnoyingPerson.PERSON_BY_NAME_AND_CHANNEL, 
                    new Param(nickName, channel));
            
            if (check != null) {
                return check;
            }
            added = new AnnoyingPerson(nickName, channel);
            w.single(added);
        }
        if (added != null) {
            final AnnoyingPersonEvent e = new AnnoyingPersonEvent(this, added);
            this.eventProvider.dispatch(PersonListener.class, e, 
                    PersonListener::personAdded);
        }
        return added;
    }



    @Override
    public synchronized AnnoyingPerson removeAnnoyingPerson(String nickName, String channel) 
            throws DatabaseException {
        
        AnnoyingPerson check = null;
        try (final Write w = this.persistence.write()) {
            final Read read = w.read();
            
            check = read.findSingle(
                    AnnoyingPerson.class, AnnoyingPerson.PERSON_BY_NAME_AND_CHANNEL, 
                    new Param(nickName, channel));
            
            if (check == null) {
                return null;
            }
            w.remove(check);
        }
        if (check != null) {
            final AnnoyingPersonEvent e = new AnnoyingPersonEvent(this, check);
            this.eventProvider.dispatch(PersonListener.class, e, 
                    PersonListener::personRemoved);
        }
        return check;
    }

}
