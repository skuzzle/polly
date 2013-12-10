package polly.rx.core.orion.model;

import polly.rx.entities.RxRessource;

public class Production {

    private RxRessource ress;
    private float rate;

    
    public Production(RxRessource ress, float rate) {
        this.ress = ress;
        this.rate = rate;
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
}
