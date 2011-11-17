package entities;


import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.FormatManager;



@Entity
@NamedQueries({
    @NamedQuery(
        name =  "OPEN_BY_USER",
        query = "SELECT t FROM TrainEntity t WHERE t.forUser = ?1 AND t.open=TRUE"),
    @NamedQuery(
        name =  "TRAINS_BY_USER",
        query = "SELECT t FROM TrainEntity t WHERE t.forUser = ?1")
})
public class TrainEntity {

    @Id@GeneratedValue
    private int id;
    
    private String forUser;
    
    private boolean open;
    
    private int cost;
    
    private String description;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    
    public static TrainEntity forString(String forUser, String input, double mod) {
        String[] parts = input.split("\\s+");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Misformatted input String");
        }
        StringBuilder b = new StringBuilder(input.length());
        DecimalFormat df = new DecimalFormat("0.00");
        b.append("(x");
        b.append(df.format(mod));
        b.append(") ");
        b.append(parts[0]); 
        b.append(" "); 
        b.append(parts[1]);
        
        int cost = 0;
        try {
            cost = Integer.parseInt(parts[2]);
            b.append(": ");
            b.append(cost);
            cost = (int) (cost * mod);
            b.append(" (");
            b.append(cost);
            b.append(")");
        } catch  (NumberFormatException e) {
            throw new IllegalArgumentException("Misformatted input String.", e);
        }
        return new TrainEntity(forUser, cost, b.toString());
    }
    

    
    public static TrainEntity forString(String forUser, String input) {
        return forString(forUser, input, 1.0); 
    }
    
    
    
    public TrainEntity() {}
    
    public TrainEntity(String forUser, int cost, String description) {
        this.forUser = forUser;
        this.cost = cost;
        this.description = description;
        this.open = true;
        this.date = new Date();
    }


    
    public String getForUser() {
        return forUser;
    }


    
    public void setForUser(String forUser) {
        this.forUser = forUser;
    }


    
    public boolean isOpen() {
        return open;
    }


    
    public void setOpen(boolean open) {
        this.open = open;
    }


    
    public int getCost() {
        return cost;
    }


    
    public void setCost(int cost) {
        this.cost = cost;
    }


    
    public String getDescription() {
        return description;
    }


    
    public void setDescription(String description) {
        this.description = description;
    }


    
    public int getId() {
        return id;
    }
    
    
    public String format(FormatManager formatter) {
        return "(" + this.getId() + ") " + this.description + " Cr. (" 
            + formatter.formatDate(this.date) + ")";
    }
    
    
    
    @Override
    public String toString() {
        return "(" + this.getId() + ") " + this.description + " Cr.";
    }
}
