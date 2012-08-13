package polly.tv.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@NamedQueries({
    @NamedQuery(
        name = "LATEST_BY_CHANNEL",
        query= "SELECT tv FROM TVEntity tv WHERE tv.channel = ?1 ORDER BY tv.start DESC"
    ),
})
public class TVEntity {
    
    public final static String LATEST_BY_CHANNEL = "LATEST_BY_CHANNEL";

    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date crawledAt;
    
    private String title;
    
    private String subtitle;
    
    private String channel;
    
    
    
    public TVEntity() {
        this(new Date(), new Date(), "", "", "");
    }
    
    
    
    public TVEntity(Date start, Date crawledAt, String channel, String title, 
            String subtitle) {
        this.start = start;
        this.crawledAt = crawledAt;
        this.channel = channel;
        this.title = title;
        this.subtitle = subtitle;
    }

    
    
    public int getId() {
        return this.id;
    }


    
    public Date getStart() {
        return this.start;
    }


    
    public Date getCrawledAt() {
        return this.crawledAt;
    }


    
    public String getTitle() {
        return this.title;
    }


    
    public String getSubtitle() {
        return this.subtitle;
    }


    
    public String getChannel() {
        return this.channel;
    }



    @Override
    public String toString() {
        return "(" + this.id + ") " + this.start.toString() + " - " + this.title + 
            (this.subtitle.equals("") ? "" : " - " + this.subtitle);
    }
}