package polly.rx.entities;

import java.util.Date;
import java.util.List;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class TrainEntityConverter {

    private PersistenceManager persistence;
    
    
    
    public TrainEntityConverter(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public void convertAllTrains() throws DatabaseException {

        this.persistence.atomicWriteOperation(new WriteAction() {
            @Override
            public void performUpdate(PersistenceManager persistence) {
                List<TrainEntity> oldTrains = persistence.atomicRetrieveList(
                        TrainEntity.class, "ALL_TRAINS");
                
                for (TrainEntity old : oldTrains) {
                    TrainEntityV2 newTrain = new TrainEntityV2(0, old.getForUser(), 
                        TrainType.parse(old.getDescription()), 1.0, old.getCost(), 
                        new Date(), new Date());
                    
                    persistence.persist(newTrain);
                    persistence.remove(old);
                }
            }
        });
    }
    
}