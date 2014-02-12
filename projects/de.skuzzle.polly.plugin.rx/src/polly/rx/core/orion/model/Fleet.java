package polly.rx.core.orion.model;

import java.util.Date;

import de.skuzzle.polly.tools.Equatable;


public interface Fleet extends Equatable, VenadOwner {

    public int getRevorixId();
    
    public String getName();
    
    public Date getDate();
    
    public Sector getSector();
}
