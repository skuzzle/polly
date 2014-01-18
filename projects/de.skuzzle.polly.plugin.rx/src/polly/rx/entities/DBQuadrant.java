package polly.rx.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import polly.rx.core.orion.QuadrantUtils;
import polly.rx.core.orion.model.OrionObjectUtil;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

@Entity
@NamedQueries({
    @NamedQuery(
        name = DBQuadrant.QUERY_ALL_QUADRANTS,
        query= "SELECT q FROM DBQuadrant q"
    ),
    @NamedQuery(
        name = DBQuadrant.QUERY_QUADRANT_BY_NAME,
        query = "SELECT q FROM DBQuadrant q WHERE q.name = ?1"
    )
})
public class DBQuadrant implements Quadrant {
    
    public final static String QUERY_ALL_QUADRANTS = "QUERY_ALL_QUADRANTS"; //$NON-NLS-1$
    public final static String QUERY_QUADRANT_BY_NAME = "QUERY_QUADRANT_BY_NAME"; //$NON-NLS-1$

    private final static String GENERATOR = "SECT_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String name;
    private int maxX;
    private int maxY;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany
    private Collection<DBSector> sectors;

    private transient final Map<String, DBSector> sectorMap;



    /**
     * @deprecated Use for JPA only
     */
    @Deprecated
    public DBQuadrant() {
        this.sectorMap = new HashMap<>();
    }



    public DBQuadrant(String name) {
        this.sectorMap = new HashMap<>();
        this.sectors = new ArrayList<>();
        this.name = name;
    }
    
    
    
    public synchronized DBSector updateFrom(Sector sector, Write write) {
        Check.objects(sector, write).notNull();
        if (!sector.getQuadName().equals(this.name)) {
            throw new IllegalArgumentException("name mismatch: " + this.name +  //$NON-NLS-1$
                    " & " + sector.getQuadName()); //$NON-NLS-1$
        } else if (sector.getType() == SectorType.NONE) {
            return null;
        }
        // sector is a new sector!
        assert !this.sectors.contains(sector);
        
        this.maxX = Math.max(this.maxX, sector.getX());
        this.maxY = Math.max(this.maxY, sector.getY());
        
        final DBSector dbSector = new DBSector(sector);
        write.single(dbSector);
        write.all(dbSector.getRessources());
        this.sectors.add(dbSector);
        this.sectorMap.put(QuadrantUtils.createMapKey(dbSector), dbSector);
        
        return dbSector;
    }



    @PostLoad
    private void createSectorMap() {
        for (final DBSector s : this.sectors) {
            this.maxX = Math.max(this.maxX, s.getX());
            this.maxY = Math.max(this.maxY, s.getY());
            this.sectorMap.put(QuadrantUtils.createMapKey(s), s);
        }
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.quadrantHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Quadrant.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.quadrantsEquals(this, (Quadrant) o);
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public DBSector getSector(int x, int y) {
        DBSector s = this.sectorMap.get(QuadrantUtils.createMapKey(x, y));
        if (s == null) {
            s = new DBSector();
            s.setQuadName(this.name);
            s.setX(x);
            s.setY(y);
            s.setType(SectorType.NONE);
            s.setDate(Time.currentTime());
            s.setRessources(new ArrayList<DBProduction>());
        }
        return s;
    }



    @Override
    public Collection<DBSector> getSectors() {
        return this.sectors;
    }



    @Override
    public int getMaxX() {
        return this.maxX;
    }



    @Override
    public int getMaxY() {
        return this.maxY;
    }



    @Override
    public String toString() {
        return OrionObjectUtil.quadrantString(this);
    }
}
