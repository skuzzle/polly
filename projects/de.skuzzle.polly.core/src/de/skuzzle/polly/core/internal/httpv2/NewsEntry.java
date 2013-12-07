package de.skuzzle.polly.core.internal.httpv2;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.core.internal.users.UserImpl;
import de.skuzzle.polly.sdk.User;

@Entity
@NamedQueries({
    @NamedQuery(
        name = NewsEntry.QUERY_GET_ALL_NEWS,
        query= "SELECT ne FROM NewsEntry ne ORDER BY ne.date DESC"
    )
})
public class NewsEntry {

    public final static String QUERY_GET_ALL_NEWS = "GET_ALL_NEWS"; //$NON-NLS-1$
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserImpl author;

    private String caption;

    private String body;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;



    public NewsEntry() {}
    

    public NewsEntry(UserImpl author, String caption, String body, Date date) {
        super();
        this.author = author;
        this.caption = caption;
        this.body = body;
        this.date = date;
    }






    public User getAuthor() {
        return this.author;
    }



    public String getCaption() {
        return this.caption;
    }



    public void setCaption(String caption) {
        this.caption = caption;
    }



    public String getBody() {
        return this.body;
    }



    public void setBody(String body) {
        this.body = body;
    }



    public Date getDate() {
        return this.date;
    }



    public void setDate(Date date) {
        this.date = date;
    }



    public int getId() {
        return this.id;
    }
}
