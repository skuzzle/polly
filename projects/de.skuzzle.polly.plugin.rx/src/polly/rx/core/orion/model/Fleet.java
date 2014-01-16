package polly.rx.core.orion.model;

import java.util.Date;


public interface Fleet {

    public int getRevorixId();
    
    public String getName();
    
    public String getOwnerName();
    
    public String getOwnerClan();
    
    public Date getDate();
    
    public Sector getSpottedAt();
}
