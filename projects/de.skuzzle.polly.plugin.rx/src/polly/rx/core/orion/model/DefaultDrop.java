package polly.rx.core.orion.model;

import java.util.EnumMap;
import java.util.Map;

import polly.rx.entities.RxRessource;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultDrop implements Drop {

    private final Map<RxRessource, Integer> drop;
    private final boolean hasArtifact;



    public DefaultDrop(Map<RxRessource, Integer> drop, boolean hasArtifact) {
        this.drop = new EnumMap<>(RxRessource.class);
        this.drop.putAll(drop);
        this.hasArtifact = hasArtifact;
    }



    public DefaultDrop(Drop other) {
        this.drop = new EnumMap<>(RxRessource.class);
        this.hasArtifact = other.hasArtifact();
        for (final RxRessource ress : RxRessource.values()) {
            this.drop.put(ress, other.getAmount(ress));
        }
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.dropHash(this);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.dropString(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Drop.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.dropEquals(this, (Drop) o);
    }



    @Override
    public int getAmount(RxRessource ress) {
        final Integer amount = this.drop.get(ress);
        if (amount == null) {
            return 0;
        }
        return amount;
    }



    @Override
    public Integer[] getAmountArray() {
        final Integer[] values = new Integer[this.drop.size()];
        this.drop.values().toArray(values);
        return values;
    }



    @Override
    public boolean hasArtifact() {
        return this.hasArtifact;
    }
}
