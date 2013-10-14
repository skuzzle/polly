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
    
    
    
    public final static void sumUp(BattleDrop[] result, BattleDrop[] values) {
        if (result.length != values.length) {
            throw new RuntimeException("dimension mismatch"); //$NON-NLS-1$
        }
        
        for (int i = 0; i < result.length; ++i) {
            BattleDrop d = values[i];
            if (result[i] == null) {
                result[i] = new BattleDrop(d.getRessource(), d.getAmount());
            } else {
                result[i].incAmout(d);
            }
        }
    }
    

    
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
    
    
    
    public void incAmout(BattleDrop other) {
        this.amount += other.amount;
    }
    
    
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    
    
    @Override
    public String toString() {
        return this.ressource + " " + this.amount; //$NON-NLS-1$
    }
}