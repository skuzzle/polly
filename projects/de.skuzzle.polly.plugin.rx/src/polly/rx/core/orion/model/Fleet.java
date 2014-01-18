package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public interface Fleet extends Equatable {

    public int getRevorixId();
    
    public String getName();
    
    public String getOwnerName();
    
    public String getOwnerClan();
    
    public Date getDate();
    
    public Sector getSector();
}
