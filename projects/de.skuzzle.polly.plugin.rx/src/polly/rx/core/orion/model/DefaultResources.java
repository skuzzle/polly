package polly.rx.core.orion.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.entities.RxRessource;

public class DefaultResources implements Resources {

    private final Map<RxRessource, Number> resources;



    public DefaultResources(Map<RxRessource, ? extends Number> drop) {
        Check.objects(drop).notNull();
        this.resources = new EnumMap<>(RxRessource.class);
        this.resources.putAll(drop);
    }



    public DefaultResources(Resources other) {
        Check.objects(other).notNull();
        this.resources = new EnumMap<>(RxRessource.class);
        for (final RxRessource ress : RxRessource.values()) {
            this.resources.put(ress, other.getAmount(ress));
        }
    }



    @Override
    public Map<RxRessource, Number> asMap() {
        return Collections.unmodifiableMap(this.resources);
    }



    @Override
    public String toString() {
        return OrionObjectUtil.resourcesString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.resourcesHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Resources.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.resourcesEquals(this, (Resources) o);
    }



    @Override
    public Number getAmount(RxRessource ress) {
        final Number amount = this.resources.get(ress);
        if (amount == null) {
            return 0;
        }
        return amount;
    }



    @Override
    public Number[] getAmountArray() {
        final Number[] values = new Number[this.resources.size()];
        this.resources.values().toArray(values);
        return values;
    }
}
