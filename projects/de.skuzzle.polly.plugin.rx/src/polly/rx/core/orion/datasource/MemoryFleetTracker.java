package polly.rx.core.orion.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import polly.rx.core.orion.FleetEvent;
import polly.rx.core.orion.FleetListener;
import polly.rx.core.orion.FleetTracker;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.model.Fleet;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.EventProviders;

public class MemoryFleetTracker implements FleetTracker {
    
    private final static long MAX_AGE = Milliseconds.fromHours(3);

    private final Map<Integer, Fleet> orionFleets;
    private final EventProvider events;


    public MemoryFleetTracker() {
        this.orionFleets = new HashMap<>();
        this.events = EventProviders.newDefaultEventProvider();
    }
    
    
    
    public void addFleetListener(FleetListener listener) {
        this.events.addListener(FleetListener.class, listener);
    }
    
    
    
    public void removeFleetListener(FleetListener listener) {
        this.events.addListener(FleetListener.class, listener);
    }
    
    
    
    private Date getThresholdDate() {
        return new Date(Time.currentTimeMillis() - MAX_AGE);
    }

    

    @Override
    public synchronized void updateOwnFleets(Collection<? extends Fleet> ownFleets)
            throws OrionException {
        
        final Date threshold = this.getThresholdDate();
        this.clearOldestFleets(this.orionFleets, threshold);
        
        for (final Fleet f : ownFleets) {
            Check.number(f.getRevorixId()).isPositiveOrZero();
            this.orionFleets.put(f.getRevorixId(), f);
        }
        this.events.dispatchEvent(FleetListener.class, 
                new FleetEvent(this, new ArrayList<>(this.orionFleets.values())), 
                FleetListener.OWN_FLEETS_UPDATED);
    }


    
    private Collection<Fleet> clearOldestFleets(Map<Integer, Fleet> fleets, 
            Date threshold) {
        final Collection<Fleet> result = new ArrayList<>();
        final Iterator<Fleet> it = fleets.values().iterator();
        while (it.hasNext()) {
            final Fleet f = it.next();
            if (f.getDate().compareTo(threshold) < 0) {
                result.add(f);
                it.remove();
            }
        }
        return result;
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
