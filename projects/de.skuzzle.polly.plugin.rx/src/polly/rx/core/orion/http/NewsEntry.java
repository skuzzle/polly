package polly.rx.core.orion.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import de.skuzzle.polly.tools.Check;
import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;


public class NewsEntry implements Equatable, Comparable<NewsEntry> {
    
    static DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm dd-MM-yyyy"); //$NON-NLS-1$
    }
    
    
    public static enum NewsType {
        ORION_FLEET,
        FLEET_SPOTTED,
        PORTAL_ADDED,
        PORTAL_REMOVED,
        PORTAL_MOVED,
        TRAINING_ADDED,
        TRAINING_FINISHED,
        BILL_CLOSED;
    }
    
    

    public final String reporter;
    public final NewsType type;
    public final Object subject;
    public final String date;
    private final transient Date compDate;
    
    
    
    public NewsEntry(String reporter, NewsType type, Object subject, Date date) {
        Check.objects(reporter, type, date).notNull();
        this.reporter = reporter;
        this.type = type;
        this.subject = subject;
        this.date = getDateFormat().format(date);
        this.compDate = date;
    }


    
    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return NewsEntry.class;
    }


    
    public NewsType getType() {
        return this.type;
    }
    
    
    
    public Date getCompDate() {
        return this.compDate;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final NewsEntry entry = (NewsEntry) o;
        return this.subject.equals(entry.subject);
    }
    
    
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int compareTo(NewsEntry o) {
        return Comparator.comparing(NewsEntry::getType)
                .thenComparing(NewsEntry::getCompDate).reversed()
                .thenComparing((n1, n2) -> {
                    if (n1.subject instanceof Comparable && 
                            n1.getClass() == n2.getClass()) {
                        return ((Comparable) n1).compareTo(n2);
                    }
                    return 0;
                })
                .compare(this, o);
    }
}