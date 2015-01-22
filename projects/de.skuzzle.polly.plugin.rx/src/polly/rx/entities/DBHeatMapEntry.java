package polly.rx.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
        @NamedQuery(name = DBHeatMapEntry.BY_VENAD_AND_SECTOR,
                query = "SELECT e FROM DBHeatMapEntry e WHERE e.ownerVenadName=?1 AND e.sector=?2"),
        @NamedQuery(name = DBHeatMapEntry.BY_VENAD_AND_QUADRANT,
                query = "SELECT e FROM DBHeatMapEntry e WHERE e.ownerVenadName=?1 AND e.sector.quadName=?2")
})
public class DBHeatMapEntry {

    public final static String BY_VENAD_AND_SECTOR = "BY_VENAD_AND_SECTOR"; //$NON-NLS-1$
    public final static String BY_VENAD_AND_QUADRANT = "BY_VENAD_AND_QUADRANT"; //$NON-NLS-1$
    private final static String GENERATOR = "FLEET_HEAT_MAP_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String ownerVenadName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private DBSector sector;

    public DBHeatMapEntry() {}

    public void setOwnerVenadName(String ownerVenadName) {
        this.ownerVenadName = ownerVenadName;
    }

    public void setSector(DBSector sector) {
        this.sector = sector;
    }

    public String getOwnerVenadName() {
        return this.ownerVenadName;
    }

    public int getId() {
        return this.id;
    }

    public DBSector getSector() {
        return this.sector;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
