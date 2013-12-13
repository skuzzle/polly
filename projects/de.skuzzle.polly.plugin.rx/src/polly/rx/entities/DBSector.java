package polly.rx.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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

import polly.rx.core.orion.model.SectorType;

@Entity
@NamedQueries({ 
    @NamedQuery(
        name = DBSector.QUERY_FIND_SECTOR, 
        query = "SELECT qs FROM DBSector qs WHERE qs.quadName = ?1 AND qs.x = ?2 AND qs.y = ?2"
    ),
    @NamedQuery(
        name = DBSector.QUERY_BY_QUADRANT, 
        query = "SELECT qs FROM DBSector qs WHERE qs.quadName = ?1"
    ),
    @NamedQuery(
        name = DBSector.QUERY_DISTINCT_QUADS,
        query = "SELECT DISTINCT(qs.quadName) FROM DBSector qs"
            ),
    @NamedQuery(
        name = DBSector.QUERY_ALL_SECTORS,
        query = "SELECT qs.quadName FROM DBSector qs"
    )
})
public class DBSector {

    public final static String QUERY_ALL_SECTORS = "ALL_SECTORS"; //$NON-NLS-1$
    public final static String QUERY_DISTINCT_QUADS = "DISTINCT_QUADS"; //$NON-NLS-1$
    public final static String QUERY_FIND_SECTOR = "FIND_SECTOR"; //$NON-NLS-1$
    public final static String QUERY_BY_QUADRANT = "SECTOR_BY_QUAD"; //$NON-NLS-1$

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

    @OneToMany
    private Collection<DBProduction> ressources;
    
    @OneToMany
    private Collection<DBSpawn> spawns;


    
    public DBSector() {}
    
    
    public DBSector(DBSector src) {
        this.quadName = src.quadName;
        this.x = src.x;
        this.y = src.y;
        this.attackerBonus = src.attackerBonus;
        this.defenderBonus = src.defenderBonus;
        this.sectorGuardBonus = src.sectorGuardBonus;
        this.date = new Date(src.date.getTime());
        this.type = src.type;
        this.ressources = new ArrayList<>(src.ressources);
        this.spawns = new ArrayList<>(src.spawns);
    }
    

    public String getQuadName() {
        return this.quadName;
    }



    public void setQuadName(String quadName) {
        this.quadName = quadName;
    }



    public int getX() {
        return this.x;
    }



    public void setX(int x) {
        this.x = x;
    }



    public int getY() {
        return this.y;
    }



    public void setY(int y) {
        this.y = y;
    }



    public Date getDate() {
        return this.date;
    }



    public void setDate(Date date) {
        this.date = date;
    }



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
    
    
    
    
    public Collection<DBSpawn> getSpawns() {
        return this.spawns;
    }
    
    
    
    public void setSpawns(Collection<DBSpawn> spawns) {
        this.spawns = spawns;
    }



    public static String getQueryFindSector() {
        return QUERY_FIND_SECTOR;
    }

    

    
    public int getAttackerBonus() {
        return this.attackerBonus;
    }



    
    public void setAttackerBonus(int attackerBonus) {
        this.attackerBonus = attackerBonus;
    }



    
    public int getDefenderBonus() {
        return this.defenderBonus;
    }



    
    public void setDefenderBonus(int defenderBonus) {
        this.defenderBonus = defenderBonus;
    }



    
    public int getSectorGuardBonus() {
        return this.sectorGuardBonus;
    }



    
    public void setSectorGuardBonus(int sectorGuardBonus) {
        this.sectorGuardBonus = sectorGuardBonus;
    }



    public int getId() {
        return this.id;
    }
}