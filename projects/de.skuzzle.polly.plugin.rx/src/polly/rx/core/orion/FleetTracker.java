package polly.rx.core.orion;

import java.util.Collection;

import polly.rx.core.orion.model.Fleet;


public interface FleetTracker {

    public Collection<? extends Fleet> getOrionUserFleets();
    
    public Collection<? extends Fleet> getFleets();
    
    public void updateFleets(Collection<? extends Fleet> fleets) throws OrionException;

    public void updateOwnFleets(Collection<? extends Fleet> ownFleets) 
            throws OrionException;
}
