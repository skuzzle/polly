package polly.rx.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ScoreBoardEntry {
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private String venadName;
    
    private int rank;
    
    private int points;

    public ScoreBoardEntry(String venadName, int rank, int points) {
        super();
        this.venadName = venadName;
        this.rank = rank;
        this.points = points;
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
    
    
    
}
