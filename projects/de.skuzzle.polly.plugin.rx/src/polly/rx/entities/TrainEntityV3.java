package polly.rx.entities;

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

import polly.rx.MSG;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.Equatable;

@Entity
@NamedQueries({
    @NamedQuery(
        name =  TrainEntityV3.OPEN_BY_USER_AND_TRAINER,
        query = "SELECT t FROM TrainEntityV3 t WHERE t.trainerId = ?1 AND LOWER(t.forUser) = LOWER(?2) AND t.closed=FALSE"),
    @NamedQuery(
        name =  TrainEntityV3.OPEN_BY_USER,
        query = "SELECT t FROM TrainEntityV3 t WHERE LOWER(t.forUser) = LOWER(?1) AND t.closed=FALSE"),
    @NamedQuery(
        name =  TrainEntityV3.CLOSED_BY_USER,
        query = "SELECT t FROM TrainEntityV3 t WHERE LOWER(t.forUser) = LOWER(?1) AND t.closed=true"),
    @NamedQuery(
        name =  TrainEntityV3.OPEN_BY_TRAINER,
        query = "SELECT t FROM TrainEntityV3 t WHERE t.trainerId = ?1 AND t.closed=false"),
    @NamedQuery(
        name =  TrainEntityV3.CLOSED_BY_TRAINER,
        query = "SELECT t FROM TrainEntityV3 t WHERE t.trainerId = ?1 AND t.closed=true"),
    @NamedQuery(
        name =  TrainEntityV3.TRAINSV2_BY_USER,
        query = "SELECT t FROM TrainEntityV3 t WHERE LOWER(t.forUser) = LOWER(?1)"),
    @NamedQuery(
        name =  TrainEntityV3.ALL_TE3,
        query = "SELECT t FROM TrainEntityV3 t")
})
public class TrainEntityV3 implements Equatable {
    
    public final static String OPEN_BY_USER_AND_TRAINER = "OPEN_BY_USER_AND_TRAINER"; //$NON-NLS-1$
    public final static String OPEN_BY_USER = "OPEN_BY_USER"; //$NON-NLS-1$
    public final static String CLOSED_BY_USER = "CLOSED_BY_USER"; //$NON-NLS-1$
    public final static String CLOSED_BY_TRAINER = "CLOSED_BY_TRAINER"; //$NON-NLS-1$
    public final static String OPEN_BY_TRAINER = "OPEN_BY_TRAINER"; //$NON-NLS-1$
    public final static String ALL_TE3 = "ALL_TE3"; //$NON-NLS-1$
    public final static String TRAINSV2_BY_USER = "TRAINSV2_BY_USER"; //$NON-NLS-1$
    
    
    
    public static TrainEntityV3 parseString(int trainerId, String forUser, double factor, 
            String trainString) {
        
        final String[] parts = trainString.split("\\s+", 7); //$NON-NLS-1$
        if (parts.length == 6) {
            /*
             * Parts:
             * [0] First part of train type
             * [1] Second part of train type
             * [2] Current value
             * [3] costs
             * [4] "Cr."
             * [5] duration
             */
            final TrainType type = TrainType.parse(parts[0]);
            
            // stripp off braces
            final int current = Integer.parseInt(
                parts[2].substring(1, parts[2].length() - 1));
            final int costs = Integer.parseInt(parts[3]);
            final Date now = Time.currentTime();
            final Date finished = parseDuration(parts[5]);
            return new TrainEntityV3(trainerId, forUser, type, factor, costs, now, 
                finished, current);
        } else if (parts.length == 7) {
            /*
             * Parts:
             * [0] First part of train type
             * [1] Second part of train type
             * [2] Third part of train type
             * [3] Current value
             * [4] costs
             * [5] "Cr."
             * [6] duration
             */
            final TrainType type = TrainType.parse(parts[0] + " " + parts[1]); //$NON-NLS-1$
            
            // stripp off braces
            final int current = Integer.parseInt(
                parts[3].substring(1, parts[3].length() - 1));
            final int costs = Integer.parseInt(parts[4]);
            final Date now = Time.currentTime();
            final Date finished = parseDuration(parts[6]);
            return new TrainEntityV3(trainerId, forUser, type, factor, costs, now, 
                finished, current);
        } else {
            throw new IllegalArgumentException(
                    MSG.bind(MSG.trainEntityMisformatted, trainString));
        }
    }
    
    
    
    private final static Pattern PATTERN = 
            Pattern.compile("((\\d) Tage )?(\\d{1,2}):(\\d{1,2}):(\\d{1,2})"); //$NON-NLS-1$
    
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
            return new Date(Time.currentTimeMillis() + dur * 1000);
        }
        return Time.currentTime();
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
    
    private int currentValue;
    
    
    
    public TrainEntityV3() {}
    
    
    
    public TrainEntityV3(int trainerId, String forUser, TrainType type, double factor,
        int costs, Date trainStart, Date trainFinished, int current) {
        super();
        this.trainerId = trainerId;
        this.forUser = forUser;
        this.type = type;
        this.factor = factor;
        this.costs = costs;
        this.trainStart = trainStart;
        this.trainFinished = trainFinished;
        this.currentValue = current;
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
    
    
    
    public int getCurrentValue() {
        return this.currentValue;
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
        final String result;
        double costs = this.costs * this.factor;
        if (this.factor == 1.0) {
            result = MSG.bind(MSG.trainEntityFormatWithFactor, 
                    this.id, 
                    this.type.toString(), 
                    this.currentValue, 
                    costs, 
                    this.costs,
                    this.factor, 
                    formatter.formatTimeSpanMs(this.getDuration()));
        } else {
            result = MSG.bind(MSG.trainEntityFormatWithoutFactor, 
                    this.id, 
                    this.type.toString(), 
                    this.currentValue, 
                    costs, 
                    formatter.formatTimeSpanMs(this.getDuration()));
        }
        return result;
    }


    
    @Override
    public String toString() {
        return "[Trainer: " + this.trainerId + ", Current:" + this.currentValue + ", " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            this.type + " " + this.costs + " Cr. Finish: " + this.trainFinished + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    
    
    @Override
    public final boolean equals(Object obj) {
        return false;
    }



    @Override
    public Class<?> getEquivalenceClass() {
        return TrainEntityV3.class;
    }



    @Override
    public boolean actualEquals(Equatable o) {
        return false;
    }
}

