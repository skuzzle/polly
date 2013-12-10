package polly.rx.core.orion;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Portal {

    private PortalType type;

    private String owner;

    private Date date;



    public Portal() {
    }



    public PortalType getType() {
        return this.type;
    }



    public void setType(PortalType type) {
        this.type = type;
    }



    public String getOwner() {
        return this.owner;
    }



    public void setOwner(String owner) {
        this.owner = owner;
    }



    public Date getDate() {
        return this.date;
    }



    public void setDate(Date date) {
        this.date = date;
    }
}
