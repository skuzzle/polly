package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Production {

    private final static String GENERATOR = "SEQ_PROD"; //$NON-NLS-1$

    @Id
    @SequenceGenerator(name = GENERATOR)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR)
    private int id;

    @Enumerated(EnumType.ORDINAL)
    private RxRessource ress;

    private float rate;



    public Production() {
    }



    public RxRessource getRess() {
        return this.ress;
    }



    public void setRess(RxRessource ress) {
        this.ress = ress;
    }



    public float getRate() {
        return this.rate;
    }



    public void setRate(float rate) {
        this.rate = rate;
    }



    public static String getGenerator() {
        return GENERATOR;
    }



    public int getId() {
        return this.id;
    }
}
