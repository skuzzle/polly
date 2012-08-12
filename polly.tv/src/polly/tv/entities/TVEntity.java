package polly.tv.entities;

import java.util.Date;



public class TVEntity {

    private int id;
    
    private Date start;
    
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
        this.title = title;
        this.subtitle = subtitle;
    }
    
    
    
    @Override
    public String toString() {
        return "(" + this.id + ") " + this.start.toString() + " - " + this.title + 
            (this.subtitle.equals("") ? "" : " - " + this.subtitle);
    }
}