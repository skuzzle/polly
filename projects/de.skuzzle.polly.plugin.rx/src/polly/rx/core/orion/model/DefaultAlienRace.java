package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultAlienRace implements AlienRace {

    private final String name;
    private final String subName;
    private final boolean aggressive;



    public DefaultAlienRace(String name, String subName, boolean aggressive) {
        Check.objects(name, subName).notNull();
        this.name = name;
        this.subName = subName;
        this.aggressive = aggressive;
    }



    public DefaultAlienRace(AlienRace other) {
        this(other.getName(), other.getSubName(), other.isAggressive());
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.alienRaceHash(this);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.alienRaceString(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return AlienRace.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.alienRaceEquals(this, (AlienRace) o);
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public String getSubName() {
        return this.subName;
    }



    @Override
    public boolean isAggressive() {
        return this.aggressive;
    }
}
