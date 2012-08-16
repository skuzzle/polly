package polly.rx.entities;


public class Drop {

    int id;
    
    private RxRessource ressource;
    
    private int amount;

    
    public Drop() {}
    
    
    
    public Drop(RxRessource ressource, int amount) {
        super();
        this.ressource = ressource;
        this.amount = amount;
    }
    
    
    
    @Override
    public String toString() {
        return this.ressource + " " + this.amount;
    }
}