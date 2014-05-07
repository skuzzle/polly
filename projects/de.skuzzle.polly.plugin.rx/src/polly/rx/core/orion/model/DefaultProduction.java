package polly.rx.core.orion.model;

import polly.rx.entities.RxRessource;
import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;

public class DefaultProduction implements Production {

    private final RxRessource ress;
    private final double rate;



    public DefaultProduction(RxRessource ress, double rate) {
        Check.objects(ress).notNull();
        this.ress = ress;
        this.rate = rate;
    }
    
    
    
    public DefaultProduction(Production p) {
        this(p.getRess(), p.getRate());
    }



    @Override
    public String toString() {
        return OrionObjectUtil.productionString(this);
    }



    @Override
    public int hashCode() {
        return OrionObjectUtil.productionHash(this);
    }



    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return Production.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return OrionObjectUtil.productionEquals(this, (Production) o);
    }



    @Override
    public RxRessource getRess() {
        return this.ress;
    }



    @Override
    public double getRate() {
        return this.rate;
    }



    @Override
    public int compareTo(Production prod) {
        return OrionObjectUtil.compareProduction(this, prod);
    }
}
