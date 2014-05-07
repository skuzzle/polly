package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import polly.rx.core.orion.model.OrionObjectUtil;
import polly.rx.core.orion.model.Production;
import polly.rx.entities.RxRessource;

@Entity
public class DBProduction implements Production {

    private final static String GENERATOR = "PROD_GEN"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    @Enumerated(EnumType.ORDINAL)
    private RxRessource ress;
    private float rate;



    public DBProduction() {
    }



    DBProduction(RxRessource ress, float rate) {
        this.ress = ress;
        this.rate = rate;
    }



    @Override
    public RxRessource getRess() {
        return this.ress;
    }



    public void setRess(RxRessource ress) {
        this.ress = ress;
    }



    @Override
    public double getRate() {
        return this.rate;
    }



    public void setRate(float rate) {
        this.rate = rate;
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
    public String toString() {
        return OrionObjectUtil.productionString(this);
    }
    
    
    
    @Override
    public int compareTo(Production o) {
        return OrionObjectUtil.compareProduction(this, o);
    }
}
