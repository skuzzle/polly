package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultAlienSpawn implements AlienSpawn {

    private final String name;
    private final DefaultAlienRace race;
    private final DefaultSector sector;



    public DefaultAlienSpawn(String name, DefaultAlienRace race, DefaultSector sector) {
        Check.objects(name, race, sector).notNull();
        this.name = name;
        this.sector = sector;
        this.race = race;
    }



    public DefaultAlienSpawn(AlienSpawn spawn) {
        this(spawn.getName(), new DefaultAlienRace(spawn.getRace()), new DefaultSector(
                spawn.getSector()));
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
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return AlienSpawn.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.alienSpawnsEqual(this, (AlienSpawn) o);
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
