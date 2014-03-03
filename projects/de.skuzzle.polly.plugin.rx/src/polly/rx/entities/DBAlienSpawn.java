package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.AlienSpawn;
import polly.rx.core.orion.model.OrionObjectUtil;
import polly.rx.core.orion.model.Sector;

@Entity
@NamedQueries({
    @NamedQuery(
        name = DBAlienSpawn.SPAWN_BY_SECTOR,
        query= "SELECT s FROM DBAlienSpawn s WHERE s.sector = ?1"
    ),
    @NamedQuery(
        name = DBAlienSpawn.ALL_SPAWNS,
        query = "SELECT s FROM DBAlienSpawn s"
    ),
    @NamedQuery(
        name = DBAlienSpawn.SPAWN_BY_QUADRANT,
        query = "SELECT s FROM DBAlienSpawn s WHERE s.sector.quadName = ?1"
    ),
    @NamedQuery(
        name = DBAlienSpawn.FIND_SPAWN,
        query= "SELECT s FROM DBAlienSpawn s WHERE s.name = ?1 AND s.race = ?2 AND s.sector = ?3"
    ),
})
public class DBAlienSpawn implements AlienSpawn {

    public final static String FIND_SPAWN = "FIND_SPAWN"; //$NON-NLS-1$
    public final static String SPAWN_BY_QUADRANT = "SPAWN_BY_QUADRANT"; //$NON-NLS-1$
    public final static String SPAWN_BY_SECTOR = "SPAWN_BY_SECTOR"; //$NON-NLS-1$
    public final static String ALL_SPAWNS = "ALL_SPAWNS"; //$NON-NLS-1$
    private final static String GENERATOR = "ALIEN_SPAWN_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    private String name;
    private DBAlienRace race;
    private DBSector sector;



    public DBAlienSpawn() {
    }



    public DBAlienSpawn(String name, DBAlienRace race, DBSector sector) {
        Check.objects(name, race, sector).notNull();
        this.name = name;
        this.race = race;
        this.sector = sector;
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.alienSpawnString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.alienSpawnHash(this);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return AlienSpawn.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.alienSpawnsEqual(this, (AlienSpawn) o);
    }



    public int getId() {
        return this.id;
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public AlienRace getRace() {
        return this.race;
    }



    @Override
    public Sector getSector() {
        return this.sector;
    }
}
