package polly.rx.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import polly.rx.core.orion.model.OrionObjectUtil;
import polly.rx.core.orion.model.Production;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

@Entity
@NamedQueries({
        @NamedQuery(name = DBSector.QUERY_FIND_SECTOR, query = "SELECT qs FROM DBSector qs WHERE qs.quadName = ?1 AND qs.x = ?2 AND qs.y = ?3"),
        @NamedQuery(name = DBSector.QUERY_BY_QUADRANT, query = "SELECT qs FROM DBSector qs WHERE qs.quadName = ?1"),
        @NamedQuery(name = DBSector.QUERY_DISTINCT_QUADS, query = "SELECT DISTINCT(qs.quadName) FROM DBSector qs"),
        @NamedQuery(name = DBSector.QUERY_SECTOR_BY_TYPE, query = "SELECT s FROM DBSector s WHERE s.type = ?1"),
        @NamedQuery(name = DBSector.QUERY_ALL_SECTORS, query = "SELECT qs.quadName FROM DBSector qs") })
public class DBSector implements Sector {

    public final static String QUERY_ALL_SECTORS = "ALL_SECTORS"; //$NON-NLS-1$
    public final static String QUERY_DISTINCT_QUADS = "DISTINCT_QUADS"; //$NON-NLS-1$
    public final static String QUERY_FIND_SECTOR = "FIND_SECTOR"; //$NON-NLS-1$
    public final static String QUERY_BY_QUADRANT = "SECTOR_BY_QUAD"; //$NON-NLS-1$
    public final static String QUERY_SECTOR_BY_TYPE = "QUERY_SECTOR_BY_TYPE"; //$NON-NLS-1$

    private final static String GENERATOR = "SECT_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String quadName;

    private int x;

    private int y;

    private int attackerBonus;

    private int defenderBonus;

    private int sectorGuardBonus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Enumerated(EnumType.ORDINAL)
    private SectorType type;

    @OneToMany(cascade = CascadeType.REMOVE)
    private Collection<DBProduction> ressources;

    

    public DBSector() {}



    public DBSector(Sector src) {
        Check.notNull(src, src.getType(), src.getRessources(), src.getQuadName(), 
                src.getDate());
        this.quadName = src.getQuadName();
        this.x = src.getX();
        this.y = src.getY();
        this.attackerBonus = src.getAttackerBonus();
        this.defenderBonus = src.getDefenderBonus();
        this.sectorGuardBonus = src.getSectorGuardBonus();
        this.date = new Date(src.getDate().getTime());
        this.type = src.getType();
        this.ressources = new ArrayList<>(src.getRessources().size());
        for (final Production prod : src.getRessources()) {
            this.ressources.add(new DBProduction(prod.getRess(), prod.getRate()));
        }
    }



    public void updateFrom(Sector other, Write write) {
        Check.notNull(other, other.getType(), other.getRessources(), 
                other.getQuadName(), other.getDate());
         if (!this.equals(other)) {
            throw new IllegalArgumentException(); // can not update distinct sectors
        } else if (other.getType() == SectorType.UNKNOWN) {
            // do not update with unknown information
            return;
        }
        this.attackerBonus = other.getAttackerBonus();
        this.defenderBonus = other.getDefenderBonus();
        this.sectorGuardBonus = other.getSectorGuardBonus();
        this.date = Time.currentTime();
        this.type = other.getType();
        
        if (!this.ressources.equals(other.getRessources())) {
            write.removeAll(this.ressources);
            this.ressources.clear();
            for (final Production prod : other.getRessources()) {
                final DBProduction dbProd = new DBProduction(prod.getRess(), prod.getRate());
                this.ressources.add(dbProd);
                write.single(dbProd);
            }
        }
    }



    @Override
    public String getQuadName() {
        return this.quadName;
    }



    public void setQuadName(String quadName) {
        this.quadName = quadName;
    }



    @Override
    public int getX() {
        return this.x;
    }



    public void setX(int x) {
        this.x = x;
    }



    @Override
    public int getY() {
        return this.y;
    }



    public void setY(int y) {
        this.y = y;
    }



    @Override
    public Date getDate() {
        return this.date;
    }



    public void setDate(Date date) {
        this.date = date;
    }



    @Override
    public SectorType getType() {
        return this.type;
    }



    public void setType(SectorType type) {
        this.type = type;
    }



    public Collection<DBProduction> getRessources() {
        return this.ressources;
    }



    public void setRessources(Collection<DBProduction> ressources) {
        this.ressources = ressources;
    }



    public static String getQueryFindSector() {
        return QUERY_FIND_SECTOR;
    }



    @Override
    public int getAttackerBonus() {
        return this.attackerBonus;
    }



    public void setAttackerBonus(int attackerBonus) {
        this.attackerBonus = attackerBonus;
    }



    @Override
    public int getDefenderBonus() {
        return this.defenderBonus;
    }



    public void setDefenderBonus(int defenderBonus) {
        this.defenderBonus = defenderBonus;
    }



    @Override
    public int getSectorGuardBonus() {
        return this.sectorGuardBonus;
    }



    public void setSectorGuardBonus(int sectorGuardBonus) {
        this.sectorGuardBonus = sectorGuardBonus;
    }



    public int getId() {
        return this.id;
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.sectorHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Sector.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        final Sector other = (Sector) o;
        return this.x == other.getX() && this.getY() == other.getY()
                && this.quadName.equals(other.getQuadName());
    }



    @Override
    public String toString() {
        return OrionObjectUtil.sectorString(this);
    }
}