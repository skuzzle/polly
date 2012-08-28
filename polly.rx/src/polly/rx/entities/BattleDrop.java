package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BattleDrop {

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    int id;
    
    @Enumerated(EnumType.ORDINAL)
    private RxRessource ressource;
    
    private int amount;

    
    public BattleDrop() {}
    
    
    
    public BattleDrop(RxRessource ressource, int amount) {
        super();
        this.ressource = ressource;
        this.amount = amount;
    }
    
    
    
    
    public RxRessource getRessource() {
        return this.ressource;
    }
    
    
    
    public int getAmount() {
        return this.amount;
    }
    
    
    
    public void incAmout(int amount) {
        this.amount += amount;
    }
    
    
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    
    
    @Override
    public String toString() {
        return this.ressource + " " + this.amount;
    }
}