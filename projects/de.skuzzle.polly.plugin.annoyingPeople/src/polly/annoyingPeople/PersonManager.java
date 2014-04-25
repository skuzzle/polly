package polly.annoyingPeople;

import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import polly.annoyingPeople.entities.AnnoyingPerson;


public interface PersonManager {
    
    public void addPersonListener(PersonListener listener);
    
    public void removePersonListener(PersonListener listener);
    
    public AnnoyingPerson addAnnoyingPerson(String nickName, String channel) 
            throws DatabaseException;
    
    public AnnoyingPerson removeAnnoyingPerson(String nickName, String channel) 
            throws DatabaseException;

    public AnnoyingPerson getAnnoyingPerson(String nickName, String channel);
}
