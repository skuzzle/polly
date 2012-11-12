package polly.rx.entities;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.time.Time;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "ALL_SBE_DISTINCT",
        query= "SELECT sbe FROM ScoreBoardEntry sbe GROUP BY sbe.venadName ORDER BY sbe.date, sbe.rank DESC"
    ),
    @NamedQuery(
        name = "SBE_BY_USER",
        query = "SELECT sbe FROM ScoreBoardEntry sbe WHERE sbe.venadName = ?1 ORDER BY sbe.date, sbe.rank DESC"
    )
})
public class ScoreBoardEntry {
    
    
    public static Collection<ScoreBoardEntry> postFilter(Collection<ScoreBoardEntry> entries) {
        TreeSet<ScoreBoardEntry> result = new TreeSet<ScoreBoardEntry>(
            new Comparator<ScoreBoardEntry>() {

            @Override
            public int compare(ScoreBoardEntry o1, ScoreBoardEntry o2) {
                return o1.getVenadName().compareTo(o2.getVenadName());
            }
        });
        
        result.addAll(entries);
        return result;
    }
    
    
    
    public final static String ALL_SBE_DISTINCT = "ALL_SBE_DISTINCT";
    public final static String SBE_BY_USER = "SBE_BY_USER";
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private String venadName;
    
    private String clan;
    
    private int rank;
    
    private int points;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    
    public ScoreBoardEntry() {}
    
    
    public ScoreBoardEntry(String venadName, String clan, int rank, int points) {
        super();
        this.venadName = venadName;
        this.clan = clan;
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
    
    
    
    public String getClan() {
        return this.clan;
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
