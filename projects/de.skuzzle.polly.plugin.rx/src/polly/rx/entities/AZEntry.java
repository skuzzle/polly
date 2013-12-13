package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(
        name = AZEntry.AZ_ENTRY_BY_USER,
        query= "SELECT e FROM AZEntry e WHERE e.byUserId = ?1"
    ),
    @NamedQuery(
        name = AZEntry.AZ_ENTRY_BY_FLEET_AND_USER,
        query= "SELECT e FROM AZEntry e WHERE e.fleetName = ?1 AND e.byUserId = ?2"
    )
})
public class AZEntry {
    
    public final static String AZ_ENTRY_BY_USER = "AZ_ENTRY_BY_USER"; //$NON-NLS-1$
    public final static String AZ_ENTRY_BY_FLEET_AND_USER = "AZ_ENTRY_BY_FLEET_AND_USER"; //$NON-NLS-1$
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private int byUserId;
    
    private String fleetName;
    
    private String az;
    
    private String jumpTime;
    
    
    public AZEntry() {
        this(1, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    
    
    public AZEntry(int byUserId, String fleetName, String az, String jumpTime) {
        this.byUserId = byUserId;
        this.fleetName = fleetName;
        this.az = az;
        this.jumpTime = jumpTime;
    }


    
    public int getId() {
        return this.id;
    }


    
    public int getByUserId() {
        return this.byUserId;
    }

    
    
    public String getFleetName() {
        return this.fleetName;
    }


    
    public String getJumpTime() {
        return this.jumpTime;
    }


    
    public String getAz() {
        return this.az;
    }
}
