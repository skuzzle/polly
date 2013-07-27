package polly.rx.entities;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.sdk.EntityConverter;
import de.skuzzle.polly.sdk.PersistenceManager;


public class V2ToV3TrainEntityConverter implements EntityConverter {

    @Override
    public List<Object> getOldEntities(PersistenceManager persistence) {
        final List<Object> result = new ArrayList<Object>();
        result.addAll(persistence.findList(TrainEntityV2.class, TrainEntityV2.ALL));
        return result;
    }
    

    
    @Override
    public Object convertEntity(Object old) {
        final TrainEntityV2 v2Entity = (TrainEntityV2) old;
        return new TrainEntityV3(v2Entity.getTrainerId(), v2Entity.getForUser(), 
            v2Entity.getType(), v2Entity.getFactor(), v2Entity.getCosts(), 
            v2Entity.getTrainStart(), v2Entity.getTrainFinished(), 0);
    }

    
    
    @Override
    public void deleteOldEntities(List<Object> olds, PersistenceManager persistence) {
        persistence.removeList(olds);
    }
    
    
    
    @Override
    public String toString() {
        return "V2ToV3TrainEintityConverter";
    }
}
