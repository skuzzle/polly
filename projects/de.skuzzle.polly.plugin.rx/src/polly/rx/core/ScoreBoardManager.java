package polly.rx.core;

import java.awt.Color;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import polly.rx.entities.ScoreBoardEntry;
import polly.rx.graphs.HighlightArea;
import polly.rx.graphs.ImageGraph;
import polly.rx.graphs.Point;
import polly.rx.graphs.Point.PointType;
import polly.rx.graphs.PointSet;
import polly.rx.graphs.YScale;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.time.DateUtils;
import de.skuzzle.polly.sdk.time.Time;


public class ScoreBoardManager {
    
    private final static Color[] COLORS = {
        Color.RED, Color.BLUE, Color.BLACK, Color.GREEN, Color.PINK
    };
    
    public final static NumberFormat NUMBER_FORMAT = DecimalFormat.getInstance(
            Locale.ENGLISH);
    static {
        ((DecimalFormat) NUMBER_FORMAT).applyPattern("0.00");
    }

    private PersistenceManager persistence;
    
    
    
    public ScoreBoardManager(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public int maxColors() {
        return COLORS.length;
    }
    
    
    
    
    public InputStream createLatestGraph(List<ScoreBoardEntry> all, int maxMonths) {
        if (all.isEmpty()) {
            return null;
        }
        Collections.sort(all, ScoreBoardEntry.BY_DATE);
        
        //final YScale pointScale = new YScale("Points", 6000, 30000, 2000);
        
        final ImageGraph g = new ImageGraph(850, 500);
        g.setxLabels(this.createXLabels(maxMonths));
        g.setDrawGridVertical(true);
        
        
        final PointSet points = new PointSet(Color.RED);
        points.setConnect(true);
        points.setStrength(1.5f);
        
        final PointSet rank = new PointSet(new Color(0, 0, 255, 128));
        rank.setConnect(true);
        
        final Point lowest = this.createPointSet(all, maxMonths, points, rank);
        
        final YScale pointScale = points.calculateScale("Points", 10);
        final YScale rankScale = rank.calculateScale("Rank", 10);
        
        points.setScale(pointScale);
        rank.setScale(rankScale);
        
        points.setName("Points");
        
        g.addPointSet(points);
        g.addPointSet(rank);
        
        g.setLeftScale(pointScale);
        g.setRightScale(rankScale);
        g.getLeftScale().setDrawGrid(true);
        
        if (lowest != null) {
            g.addHighlightArea(new HighlightArea("Interpolated", 0, lowest.getX(), 
                new Color(0, 0, 0, 20)));
        }
        
        g.updateImage();
        return g.getBytes();
    }
    
    
    
    public InputStream createMultiGraph(int maxMonths, String...names) {
        ImageGraph g = new ImageGraph(850, 500);
        
        g.setxLabels(this.createXLabels(maxMonths));

        
        g.setDrawGridVertical(true);
        final PointSet common = new PointSet(); // just for calculating common scale
        int max = Math.min(COLORS.length, names.length);
        for (int i = 0; i < max; ++i) {
            final List<ScoreBoardEntry> entries = this.getEntries(names[i]);
            Collections.sort(entries, ScoreBoardEntry.BY_DATE);
            final Color next = COLORS[i];
            final PointSet left = new PointSet(next);
            left.setConnect(true);
            this.createPointSet(entries, maxMonths, left, new PointSet());
            common.addAll(left);
            g.addPointSet(left);
        }
        
        final YScale pointScale = common.calculateScale("Points", 10);
        pointScale.setDrawGrid(true);
        for (final PointSet ps : g.getData()) {
            ps.setScale(pointScale);
        }
        g.setLeftScale(pointScale);
        g.updateImage();
        return g.getBytes();
    }
    
    
    
    private final String[] createXLabels(int maxMonths) {
        final DateFormat df = new SimpleDateFormat("MMM yyyy");
        
        String[] labels = new String[maxMonths];
        final Date today = Time.currentTime();
        for (int i = 0; i < maxMonths; ++i) {
            final Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.MONTH, -(maxMonths - (i + 2)));
            labels[i] = df.format(c.getTime());
        }
        return labels;
    }
    
    
    
    private Point createPointSet(List<ScoreBoardEntry> entries,
            int maxMonths, PointSet left, PointSet right) {
        
        if (entries.size() < 2) {
            return null;
        }
        
        final ScoreBoardEntry oldest = entries.get(0);
        
        final Date today = Time.currentTime();
        Point greatestLowerZeroPoints = null;
        Point lowestGreaterZeroPoints = null;
        
        Point greatestLowerZeroRank = null;
        Point lowestGreaterZeroRank = null;
        
        for (final ScoreBoardEntry entry : entries) {
            final int monthsAgo = this.getMonthsAgo(today, entry.getDate(), maxMonths);
            
            final double x = this.calcX(entry.getDate(), monthsAgo);
            final Point points = new Point(x, entry.getPoints(), PointType.NONE);
            final Point rank = new Point(x, entry.getRank(), PointType.NONE);
            
            if (x < 0.0 && (greatestLowerZeroPoints == null || greatestLowerZeroPoints.getX() < x)) {
                greatestLowerZeroPoints = points;
                greatestLowerZeroRank = rank;
            }
            if (x > 0.0 && (lowestGreaterZeroPoints == null || lowestGreaterZeroPoints.getX() > x)) {
                lowestGreaterZeroPoints = points;
                lowestGreaterZeroRank = rank;
            }

            if (monthsAgo <= 0) {
                // do not add points that are older than X_LABELS months
                continue;
            }
            
            right.add(rank);
            left.add(points);
        }
        
        if (entries.size() > 1 && Math.abs(
                    DateUtils.monthsBetween(today, oldest.getDate())) > maxMonths) {
            
            // interpolate correct y-axis intersection for points
            double m = (lowestGreaterZeroPoints.getY() - greatestLowerZeroPoints.getY()) / 
                    (lowestGreaterZeroPoints.getX() - greatestLowerZeroPoints.getX());
            double y = m * (-lowestGreaterZeroPoints.getX()) + lowestGreaterZeroPoints.getY();
            final Point zero = new Point(0.0, y, PointType.DOT); 
            left.add(zero);
            left.remove(lowestGreaterZeroPoints);
            left.add(new Point(lowestGreaterZeroPoints.getX(), 
                lowestGreaterZeroPoints.getY(), PointType.X));
            
            m = (lowestGreaterZeroRank.getY() - greatestLowerZeroRank.getY()) / 
                (lowestGreaterZeroRank.getX() - greatestLowerZeroRank.getX());
            y = m * (-lowestGreaterZeroRank.getX()) + lowestGreaterZeroRank.getY();
            right.add(new Point(0, y, PointType.DOT));
            right.remove(lowestGreaterZeroRank);
            right.add(new Point(lowestGreaterZeroRank.getX(), 
                lowestGreaterZeroRank.getY(), PointType.X));
        }
        
        left.setName(oldest.getVenadName());
        right.setName("Rank");
        return lowestGreaterZeroPoints;
    }
    
    
    

    private int getMonthsAgo(Date today, Date other, int maxMonths) {
        final int monthsBetween = DateUtils.monthsBetween(today, other);
        final int monthsAgo =  maxMonths - monthsBetween - 1;
        return monthsAgo;
    }
    
    
    
    private double calcX(Date d, int monthsAgo) {
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        final int dayInMonth = c.get(Calendar.DAY_OF_MONTH);
        final int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        final double x = (monthsAgo - 1) + (double) dayInMonth / (double) days; 
        return x;
    }


    
    public void addEntries(Collection<ScoreBoardEntry> entries) throws DatabaseException {
        try {
            this.persistence.writeLock();
            final List<ScoreBoardEntry> toAdd = new ArrayList<>(50);
            
            for (final ScoreBoardEntry entry : entries) {
                final Collection<ScoreBoardEntry> existing = this.persistence.findList(
                        ScoreBoardEntry.class, ScoreBoardEntry.SBE_BY_USER,
                        new Object[] { entry.getVenadName() });
                
                boolean exists = false;
                for (final ScoreBoardEntry e : existing) {
                    // skip entry if it is from same day and has identical points
                    final boolean skip = e.getPoints() == entry.getPoints() && 
                        DateUtils.isSameDay(e.getDate(), entry.getDate());
                    exists |= skip;
                    if (skip) {
                        break;
                    }
                }
                
                if (!exists) {
                    toAdd.add(entry);
                }
            }
            
            if (!toAdd.isEmpty()) {
                this.persistence.startTransaction();
                this.persistence.persistList(toAdd);
                this.persistence.commitTransaction();
            }
        } finally {
            this.persistence.writeUnlock();
        }
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
