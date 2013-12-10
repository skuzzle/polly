package polly.rx.entities;

import polly.rx.entities.RxRessource;

public class DBProduction {

    private RxRessource ress;
    private float rate;



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
