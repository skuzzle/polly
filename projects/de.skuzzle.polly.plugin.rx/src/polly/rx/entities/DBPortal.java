package polly.rx.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.core.orion.model.OrionObjectUtil;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.PortalType;

@Entity
@NamedQueries({
    @NamedQuery(name = DBPortal.QUERY_PORTAL_BY_TYPE_AND_OWNER, query = "SELECT p FROM DBPortal p WHERE p.type = ?1 AND p.ownerName = ?2"),
    @NamedQuery(name = DBPortal.QUERY_PORTAL_BY_TYPE_AND_CLANTAG, query = "SELECT p FROM DBPortal p WHERE p.type = ?1 AND p.ownerClan = ?2"),
    @NamedQuery(name = DBPortal.QUERY_PORTAL_BY_SECTOR, query = "SELECT p FROM DBPortal p WHERE p.sector = ?1"),
    @NamedQuery(name = DBPortal.QUERY_PORTAL_BY_TYPE_AND_SECTOR, query = "SELECT p FROM DBPortal p WHERE p.type = ?1 AND p.sector = ?2"),
    @NamedQuery(name = DBPortal.QUERY_PORTAL_BY_QUAD, query = "SELECT p FROM DBPortal p WHERE p.type = ?1 AND p.sector.quadName = ?2") })
public class DBPortal implements Portal {

    public final static String QUERY_PORTAL_BY_TYPE_AND_OWNER = "QUERY_PORTAL_BY_TYPE_AND_OWNER"; //$NON-NLS-1$
    public final static String QUERY_PORTAL_BY_TYPE_AND_CLANTAG = "QUERY_PORTAL_BY_TYPE_AND_CLANTAG"; //$NON-NLS-1$
    public static final String QUERY_PORTAL_BY_SECTOR = "QUERY_PORTAL_BY_SECTOR"; //$NON-NLS-1$
    public static final String QUERY_PORTAL_BY_QUAD = "QUERY_PORTAL_BY_QUAD"; //$NON-NLS-1$
    public final static String QUERY_PORTAL_BY_TYPE_AND_SECTOR = "QUERY_PORTAL_BY_TYPE_AND_SECTOR"; //$NON-NLS-1$
    
    private final static String GENERATOR = "PORTAL_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String ownerName;
    private String ownerClan;

    @Enumerated(EnumType.ORDINAL)
    private PortalType type;

    @ManyToOne
    private DBSector sector;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;



    public DBPortal() {
        this.date = Time.currentTime();
        this.ownerClan = ""; //$NON-NLS-1$
    }
    
    
    
    public DBPortal(String ownerName, String ownerClan, PortalType type, 
            DBSector sector, Date date) {
        Check.notNull(ownerName, ownerClan, type, sector, date);
        this.ownerName = ownerName;
        this.ownerClan = ownerClan;
        this.type = type;
        this.sector = sector;
        this.date = date;
    }



    @Override
    public DBSector getSector() {
        return this.sector;
    }

    
    
    public void setSector(DBSector sector) {
        this.sector = sector;
    }


    @Override
    public String toString() {
        return OrionObjectUtil.portalString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.portalHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Portal.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.portalsEqual(this, (Portal) o);
    }



    @Override
    public String getOwner() {
        return this.ownerName;
    }



    @Override
    public String getOwnerClan() {
        return this.ownerClan;
    }



    @Override
    public PortalType getType() {
        return this.type;
    }



    @Override
    public Date getDate() {
        return this.date;
    }
}