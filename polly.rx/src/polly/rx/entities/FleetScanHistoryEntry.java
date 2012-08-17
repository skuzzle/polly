package polly.rx.entities;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
public class FleetScanHistoryEntry {

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
    
    @ElementCollection
    private List<String> changes;
    
    
    
    public FleetScanHistoryEntry() {
        this(new Date());
    }
    
    
    
    public FleetScanHistoryEntry(Date date) {
        this.date = new Date(date.getTime());
        this.changes = new LinkedList<String>();
    }
    
    
    
    public int getId() {
        return this.id;
    }
    
    
    
    public Date getDate() {
        return this.date;
    }
    
    
    
    public List<String> getChanges() {
        return this.changes;
    }
}