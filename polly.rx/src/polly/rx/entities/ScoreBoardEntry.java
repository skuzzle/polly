package polly.rx.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import de.skuzzle.polly.sdk.time.Time;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "ALL_SBE_DISTINCT",
        query= "SELECT DISTINCT(sbe.name) FROM ScoreBoardEntry sbe ORDER BY sbe.date DESC"
    ),
    @NamedQuery(
        name = "SBE_BY_USER",
        query = "SELECT sbe FROM ScoreBoardEntry sbe WHERE sbe.name = ?1"
    )
})
public class ScoreBoardEntry {
    
    public final static String ALL_SBE_DISTINCT = "ALL_SBE_DESTINCT";
    public final static String SBE_BY_USER = "SBE_BY_USER";
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private String venadName;
    
    private int rank;
    
    private int points;

    private Date date;
    
    
    public ScoreBoardEntry(String venadName, int rank, int points) {
        super();
        this.venadName = venadName;
        this.rank = rank;
        this.points = points;
        this.date = Time.currentTime();
    }

    
    public int getId() {
        return this.id;
    }

    
    public String getVenadName() {
        return this.venadName;
    }

    
    public int getRank() {
        return this.rank;
    }

    
    public int getPoints() {
        return this.points;
    }
    
    
    
    public Date getDate() {
        return this.date;
    }
}
