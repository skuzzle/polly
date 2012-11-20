package polly.rx.core;

import java.awt.Color;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.DateUtils;
import de.skuzzle.polly.sdk.time.Time;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.graphs.ImageGraph;
import polly.rx.graphs.Point;
import polly.rx.graphs.PointSet;
import polly.rx.graphs.Point.PointType;


public class ScoreBoardManager {
    
    public final static int X_LABELS = 24;
    
    public final static NumberFormat NUMBER_FORMAT = DecimalFormat.getInstance(
            Locale.ENGLISH);
    static {
        ((DecimalFormat) NUMBER_FORMAT).applyPattern("0.00");
    }

    private PersistenceManager persistence;
    
    
    
    public ScoreBoardManager(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public InputStream createGraph(List<ScoreBoardEntry> all) {
        if (all.isEmpty()) {
            return null;
        }
        final DateFormat df = new SimpleDateFormat("MMM yyyy");
        Collections.sort(all, ScoreBoardEntry.BY_DATE);
        
        String[] labels = new String[X_LABELS];
        final ScoreBoardEntry oldest = all.get(0);
        final Date today = Time.currentTime();
        for (int i = 0; i < X_LABELS; ++i) {
            final Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.MONTH, -(X_LABELS - (i + 1)));
            labels[i] = df.format(c.getTime());
        }
        
        final ImageGraph g = new ImageGraph(700, 400, 2000, 30000, 2000);
        g.setxLabels(labels);
        g.setDrawGridHorizontal(true);
        g.setDrawGridVertical(true);
        g.setConnect(true);
        
        boolean zero = false;
        final PointSet points = new PointSet(Color.RED);
        for (final ScoreBoardEntry entry : all) {
            final int monthsBetween = DateUtils.monthsBetween(
                today, entry.getDate());
            final int monthsAgo =  X_LABELS - monthsBetween - 1; 
            
            zero |= monthsAgo == 0;
            if (monthsAgo < 0) {
                // do not add points that are older than X_LABELS months
                continue;
            }
            final Calendar c = Calendar.getInstance();
            c.setTime(entry.getDate());
            final int dayInMonth = c.get(Calendar.DAY_OF_MONTH);
            final int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            final double x = monthsAgo + (double) dayInMonth / (double) days; 
            points.add(x, entry.getPoints(), PointType.NONE);
        }
        if (!zero && Math.abs(
                    DateUtils.monthsBetween(today, oldest.getDate())) > X_LABELS) {
            points.add(new Point(0.0, oldest.getPoints(), PointType.DOT));
        }
        
        g.setPointSet(points);
        g.updateImage();
        return g.getBytes();
    }
    
    
    
    public void addEntry(final ScoreBoardEntry entry) throws DatabaseException {
        
        Collection<ScoreBoardEntry> existing = this.getEntries(entry.getVenadName());
        
        // skip identical entries.
        for (ScoreBoardEntry e : existing) {
            if (DateUtils.isSameDay(e.getDate(), entry.getDate())) {
                return;
            }
        }
        
        this.persistence.atomicWriteOperation(new WriteAction() {
            @Override
            public void performUpdate(PersistenceManager persistence) {
                persistence.persist(entry);
            }
        });
    }
    
    
    
    public void deleteEntry(int id) throws DatabaseException {
        ScoreBoardEntry sbe = this.persistence.atomicRetrieveSingle(
            ScoreBoardEntry.class, id);
        
        if (sbe == null) { 
            return;
        }
        
        this.persistence.atomicRemove(sbe);
    }
    
    
    
    
    public List<ScoreBoardEntry> getEntries(String venad) {
        return this.persistence.atomicRetrieveList(ScoreBoardEntry.class, 
            ScoreBoardEntry.SBE_BY_USER, venad);
    }
    
    
    
    public List<ScoreBoardEntry> getEntries() {
        return this.persistence.atomicRetrieveList(ScoreBoardEntry.class, 
            ScoreBoardEntry.ALL_SBE_DISTINCT);
    }
    
}
