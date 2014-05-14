package polly.rx.core.orion.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polly.rx.core.orion.FleetEvent;
import polly.rx.core.orion.FleetListener;
import polly.rx.core.orion.FleetTracker;
import polly.rx.core.orion.OrionException;
import polly.rx.core.orion.model.Fleet;
import polly.rx.core.orion.model.Quadrant;
import de.skuzzle.jeve.EventProvider;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.collections.TemporaryValueMap;

public class MemoryFleetTracker implements FleetTracker {
    
    private final static long MAX_AGE = Milliseconds.fromHours(3);
    private final static long ORION_MAX_AGE = Milliseconds.fromMinutes(1);
    

    private final TemporaryValueMap<Integer, Fleet> orionFleets;
    private final TemporaryValueMap<String, LinkedList<Fleet>> fleets;
    private final EventProvider events;

    

    public MemoryFleetTracker() {
        this.orionFleets = new TemporaryValueMap<>(ORION_MAX_AGE);
        this.fleets = new TemporaryValueMap<>(MAX_AGE);
        this.events = EventProvider.newDefaultEventProvider();
    }
    
    
    
    @Override
    public void addFleetListener(FleetListener listener) {
        this.events.addListener(FleetListener.class, listener);
    }
    
    
    
    @Override
    public void removeFleetListener(FleetListener listener) {
        this.events.addListener(FleetListener.class, listener);
    }
    
    
    
    private Date getThresholdDate() {
        return new Date(Time.currentTimeMillis() - MAX_AGE);
    }

    

    @Override
    public synchronized void updateOrionFleets(String reporter, 
            Collection<? extends Fleet> ownFleets) throws OrionException {
        
        for (final Fleet f : ownFleets) {
            Check.number(f.getRevorixId()).isPositiveOrZero();
            this.orionFleets.put(f.getRevorixId(), f);
        }
        this.events.dispatch(FleetListener.class, 
                new FleetEvent(this, reporter, new ArrayList<>(ownFleets)), 
                FleetListener::ownFleetsUpdated);
    }


    
    private void clearOldestFleets(Collection<Fleet> fleets, Fleet current,
            Date threshold) {
        final Iterator<Fleet> it = fleets.iterator();
        while (it.hasNext()) {
            final Fleet f = it.next();
            if (f.getDate().compareTo(threshold) < 0 || f.equals(current)) {
                it.remove();
            }
        }
    }
    
    

    @Override
    public synchronized Collection<? extends Fleet> getOrionUserFleets() {
        return this.orionFleets.values();
    }



    @Override
    public Collection<? extends Fleet> getFleets() {
        final List<Fleet> result = new ArrayList<>();
        synchronized (this.orionFleets) {
            result.addAll(this.orionFleets.values());
        }
        synchronized (this.fleets) {
            for (final LinkedList<Fleet> fleets : this.fleets.values()) {
                result.addAll(fleets);
            }
        }
        return result;
    }



    @Override
    public void updateFleets(String reporter, 
            Collection<? extends Fleet> fleets) throws OrionException {
        final Date threshold = this.getThresholdDate();
        synchronized (this.fleets) {
            for (final Fleet fleet : fleets) {
                LinkedList<Fleet> existing = this.fleets.get(fleet.getOwnerName());
                if (existing == null) {
                    existing = new LinkedList<>();
                }
                
                this.clearOldestFleets(existing, fleet, threshold);
                existing.addFirst(fleet);
                // always put, to prevent automatic deletion 
                this.fleets.put(fleet.getName(), existing);
            }
        }
        
        this.events.dispatch(FleetListener.class, 
                new FleetEvent(this, reporter, new ArrayList<>(fleets)), 
                FleetListener::fleetsUpdated);
    }



    @Override
    public synchronized Collection<? extends Fleet> getFleets(Quadrant quadrant) {
        final Collection<Fleet> result = new ArrayList<>();
        for (final Fleet f : this.getFleets()) {
            if (f.getSector().getQuadName().equals(quadrant.getName())) {
                result.add(f);
            }
        }
        return result;
    }
}
