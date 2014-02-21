package polly.rx.core.orion.model;

import java.util.List;


public interface FleetScan extends VenadOwner, OrionObject {

    public Sector getSector();
    
    public int getSens();
    
    public String getFleetTag();
    
    public List<? extends ScanShip> getShips();
}