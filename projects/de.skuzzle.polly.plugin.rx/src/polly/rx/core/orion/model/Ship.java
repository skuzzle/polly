package polly.rx.core.orion.model;

import polly.rx.entities.ShipType;
import de.skuzzle.polly.tools.Equatable;


public interface Ship extends OrionObject, Equatable {

    public String getShipName();
    
    public int getShipClass();
    
    public int getRxId();
    
    public ShipType getType();
}