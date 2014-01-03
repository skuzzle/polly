package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

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

    
    
    public DBProduction() {}
    
    
    
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
    public float getRate() {
        return this.rate;
    }



    public void setRate(float rate) {
        this.rate = rate;
    }
}
