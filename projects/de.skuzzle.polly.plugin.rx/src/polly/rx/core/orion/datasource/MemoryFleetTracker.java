package polly.rx.core.orion.datasource;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import polly.rx.core.orion.FleetTracker;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.model.Fleet;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;

public class MemoryFleetTracker implements FleetTracker {
    
    private final static class FleetKey {
        final String venadName;
        final int fleetId;
        
        
        public FleetKey(Fleet f) {
            this.venadName = f.getOwnerName();
            this.fleetId = f.getRevorixId();
        }
        
        
        
        @Override
        public int hashCode() {
            return Objects.hash(this.venadName, this.fleetId);
        }
    }
    
    

    private final Map<FleetKey, Fleet> orionFleets;



    public MemoryFleetTracker() {
        this.orionFleets = new HashMap<>();
    }
    
    
    
    private Date getThresholdDate() {
        return new Date(Time.currentTimeMillis() - Milliseconds.fromHours(3));
    }



    @Override
    public synchronized void updateOwnFleets(Collection<? extends Fleet> ownFleets)
            throws OrionException {
        
        /*final Date threshold = this.getThresholdDate();
        this.clearOldestFleets(this.orionFleets, threshold);
        for (final Fleet f : ownFleets) {
            this.orionFleets.put(new FleetKey(f), f);
        }*/
        
    }


    
    private void clearOldestFleets(Map<FleetKey, Fleet> fleets, Date threshold) {
        final Iterator<Fleet> it = fleets.values().iterator();
        for(Fleet f = it.next(); it.hasNext(); f = it.next()) {
            if (f.getDate().compareTo(threshold) < 0) {
                it.remove();
            }
        }
    }
    
    

    @Override
    public Collection<? extends Fleet> getOrionUserFleets() {
        return null;
    }



    @Override
    public Collection<? extends Fleet> getFleets() {
        return null;
    }



    @Override
    public void updateFleets(Collection<? extends Fleet> fleets) throws OrionException {
    }

}
