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
    
    
    public static void clear(BattleDrop[] array) {
        for (int i = 0; i < array.length; ++i) {
            final BattleDrop br = array[i];
            RxRessource ress = null;
            if (br == null && array.length != 14) {
                throw new IllegalArgumentException("can not determine ress type"); //$NON-NLS-1$
            } else if (br != null) {
                ress = br.getRessource();
            } else {
                ress = RxRessource.values()[i];
            }
            array[i] = new BattleDrop(ress, 0);
        }
    }
    
    
    
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
    
    
    
    public final static void diff(BattleDrop[] op1, BattleDrop[] op2, BattleDrop result[]) {
        assert op1.length == op2.length && op1.length == result.length;
        
        for (int i = 0; i < result.length; ++i) {
            assert op1[i] != null || op2[i] != null;
            final RxRessource ress = op1[i] == null ? op2[i].getRessource() : op1[i].getRessource();
            
            final int op1Amount = op1[i] == null ? 0 : op1[i].getAmount();
            final int op2Amount = op2[i] == null ? 0 : op2[i].getAmount();
            result[i] = new BattleDrop(ress, op1Amount - op2Amount);
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