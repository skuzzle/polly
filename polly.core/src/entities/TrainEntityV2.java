package entities;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.FormatManager;

@Entity
@NamedQueries({
    @NamedQuery(
        name =  "OPEN_BY_USER_AND_TRAINER",
        query = "SELECT t FROM TrainEntityV2 t WHERE t.trainerId = ?1 AND t.forUser = ?2 AND t.closed=FALSE"),
    @NamedQuery(
        name =  "OPEN_BY_USER",
        query = "SELECT t FROM TrainEntityV2 t WHERE t.forUser = ?1 AND t.closed=FALSE"),
    @NamedQuery(
        name =  "CLOSED_BY_USER",
        query = "SELECT t FROM TrainEntityV2 t WHERE t.forUser = ?1 AND t.closed=true"),
    @NamedQuery(
            name =  "OPEN_BY_TRAINER",
            query = "SELECT t FROM TrainEntityV2 t WHERE t.trainerId = ?1 AND t.closed=false"),
    @NamedQuery(
            name =  "CLOSED_BY_TRAINER",
            query = "SELECT t FROM TrainEntityV2 t WHERE t.trainerId = ?1 AND t.closed=true"),
    @NamedQuery(
        name =  "TRAINSV2_BY_USER",
        query = "SELECT t FROM TrainEntityV2 t WHERE t.forUser = ?1")
})
public class TrainEntityV2 {
    
    
    public final static String OPEN_BY_USER_AND_TRAINER = "OPEN_BY_USER_AND_TRAINER";
    public final static String OPEN_BY_USER = "OPEN_BY_USER";
    public final static String CLOSED_BY_USER = "CLOSED_BY_USER";
    public final static String CLOSED_BY_TRAINER = "CLOSED_BY_TRAINER";
    public final static String OPEN_BY_TRAINER = "OPEN_BY_TRAINER";
    
    
    public static TrainEntityV2 parseString(int trainerId, String forUser, double factor, 
            String trainString) {
        /*
         * Parts:
         * [0] First part of train type
         * [1] Second part of train type
         * [2] costs
         * [3] "Cr."
         * [4] duration
         */
        String[] parts = trainString.split("\\s+", 5);
        if (parts.length != 5) {
            throw new IllegalArgumentException("ungültiges TrainEntity");
        }
        TrainType type = TrainType.parse(parts[0]);
        int costs = Integer.parseInt(parts[2]);
        Date now = new Date();
        Date finished = parseDuration(parts[4]);
        return new TrainEntityV2(trainerId, forUser, type, factor, costs, now, finished);
    }
    
    
    private final static Pattern PATTERN = 
            Pattern.compile("((\\d) Tage )?(\\d{1,2}):(\\d{1,2}):(\\d{1,2})");
    
    private static Date parseDuration(String duration) {
        Matcher m = PATTERN.matcher(duration);
        /*
         * Groups:
         * [0] entire string
         * [2] days
         * [3] hours
         * [4] minutes
         * [5] seconds
         */
        if (m.matches()) {
            int days = 0;
            if (m.start(2) != -1) {
                days = Integer.parseInt(duration.substring(m.start(2), m.end(2)));
            }
            int hours = Integer.parseInt(duration.substring(m.start(3), m.end(3)));
            int minutes = Integer.parseInt(duration.substring(m.start(4), m.end(4)));
            int seconds = Integer.parseInt(duration.substring(m.start(5), m.end(5)));
            long dur = seconds + minutes * 60 + hours * 60 * 60 + days * 24 * 60 * 60;
            return new Date(System.currentTimeMillis() + dur * 1000);
        }
        return null;
    }
    
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private int trainerId;
    
    private String forUser;
    
    private TrainType type;
    
    private double factor;
    
    private int costs;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date trainStart;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date trainFinished;

    private boolean closed;
    
    
    
    
    public TrainEntityV2() {}
    
    
    
    public TrainEntityV2(int trainerId, String forUser, TrainType type, double factor,
        int costs, Date trainStart, Date trainFinished) {
        super();
        this.trainerId = trainerId;
        this.forUser = forUser;
        this.type = type;
        this.factor = factor;
        this.costs = costs;
        this.trainStart = trainStart;
        this.trainFinished = trainFinished;
    }
    
    
    
    public int getId() {
        return this.id;
    }

    
    
    public String getForUser() {
        return this.forUser;
    }

    
    
    public int getTrainerId() {
        return this.trainerId;
    }


    
    public TrainType getType() {
        return this.type;
    }


    
    public double getFactor() {
        return this.factor;
    }


    
    public int getCosts() {
        return this.costs;
    }


    
    public Date getTrainStart() {
        return this.trainStart;
    }


    
    public Date getTrainFinished() {
        return this.trainFinished;
    }
    
    
    
    public boolean isClosed() {
        return this.closed;
    }
    
    
    
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
    
    
    
    public long getDuration() {
        if (this.trainFinished != null) {
            return this.trainFinished.getTime() - this.trainStart.getTime();
        }
        return 0;
    }
    
    
    
    public String format(FormatManager formatter) {
        StringBuilder b = new StringBuilder();
        b.append("(");
        b.append(this.id);
        b.append(") ");
        b.append(this.type.toString());
        b.append(" ");
        double costs = this.costs;
        
        if (this.factor != 1.0f) {
            costs = this.costs * this.factor;
        }
        b.append(costs);
        b.append(" Cr.");
        if (this.factor != 1.0) {
            b.append(" (");
            b.append(this.costs);
            b.append(" x ");
            b.append(this.factor);
            b.append(")");
        }
        b.append(" Dauer: ");
        b.append(formatter.formatTimeSpan(this.getDuration() / 1000));
        return b.toString();
    }


    
    @Override
    public String toString() {
        return "[Trainer: " + this.trainerId + ", " + this.type + " " + this.costs + 
            " Cr. Finish: " + this.trainFinished + "]";
    }
}

