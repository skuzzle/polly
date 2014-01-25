package polly.rx.core;

import java.awt.Color;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import polly.rx.MSG;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.graphs.HighlightArea;
import polly.rx.graphs.ImageGraph;
import polly.rx.graphs.NamedPoint;
import polly.rx.graphs.Point;
import polly.rx.graphs.Point.PointType;
import polly.rx.graphs.PointSet;
import polly.rx.graphs.YScale;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
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
        ((DecimalFormat) NUMBER_FORMAT).applyPattern("0.00"); //$NON-NLS-1$
    }

    
    private final static int AVERAGE_ELEMENTS = 10;
    
    private PersistenceManagerV2 persistence;
    
    
    
    public ScoreBoardManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    
    
    public int maxColors() {
        return COLORS.length;
    }
    
    
    
    
    public OutputStream createLatestGraph(List<ScoreBoardEntry> all, int maxMonths, 
            Collection<NamedPoint> allPoints) {
        if (all.isEmpty()) {
            return null;
        }
        Collections.sort(all, ScoreBoardEntry.BY_DATE);
        
        final ImageGraph g = new ImageGraph(850, 500);
        g.setxLabels(this.createXLabels(maxMonths));
        g.setDrawGridVertical(true);
        
        
        final PointSet points = new PointSet(Color.RED);
        points.setConnect(true);
        points.setStrength(2f);
        
        final PointSet rank = new PointSet(new Color(0, 0, 255, 128));
        rank.setConnect(true);
        rank.setStrength(2f);
        
        final PointSet averagePoints = new PointSet(Color.RED);
        averagePoints.setConnect(true);
        averagePoints.setName(MSG.bind(MSG.scoreboardAvgPoints, AVERAGE_ELEMENTS));
        
        final PointSet averageRank = new PointSet(new Color(0, 0, 255, 128));
        averageRank.setConnect(true);
        averageRank.setName(MSG.bind(MSG.scoreboardAvgRank, AVERAGE_ELEMENTS));
        final Point lowest = this.createPointSet(all, maxMonths, points, rank, 
            averagePoints, averageRank);
        
        YScale pointScale = points.calculateScale(MSG.scoreboardPoints, 10);
        if (pointScale == null) {
            pointScale = new YScale(MSG.scoreboardPoints, 2000, 30000, 2000);
        }
        YScale rankScale = rank.calculateScale(MSG.scoreboardRank, 10);
        if (rankScale == null) {
            rankScale = new YScale(MSG.scoreboardRank, 0, 1000, 10);
        }
        
        points.setScale(pointScale);
        averagePoints.setScale(pointScale);
        rank.setScale(rankScale);
        averageRank.setScale(rankScale);
        
        points.setName(MSG.scoreboardPoints);
        
        g.addPointSet(points);
        g.addPointSet(rank);
//        g.addPointSet(averagePoints);
//        g.addPointSet(averageRank);
        
        g.setLeftScale(pointScale);
        g.setRightScale(rankScale);
        g.getLeftScale().setDrawGrid(true);
        
        if (lowest != null) {
            g.addHighlightArea(new HighlightArea("", 0, lowest.getX(),  //$NON-NLS-1$
                new Color(0, 0, 0, 20)));
        }
        
        g.updateImage();
        allPoints.addAll(g.getRawPointsFromLastDraw());
        return g.getBytes();
    }
    
    
    
    public OutputStream createMultiGraph(int maxMonths, Collection<NamedPoint> allPoints, 
            String...names) {
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
            this.createPointSet(entries, maxMonths, left, new PointSet(), new PointSet(), 
                new PointSet());
            common.addAll(left);
            g.addPointSet(left);
        }
        
        YScale pointScale = common.calculateScale(MSG.scoreboardPoints, 10);
        if (pointScale == null) {
            pointScale = new YScale(MSG.scoreboardPoints, 6000, 30000, 2000);
        }
        pointScale.setDrawGrid(true);
        for (final PointSet ps : g.getData()) {
            ps.setScale(pointScale);
        }
        g.setLeftScale(pointScale);
        g.updateImage();
        allPoints.addAll(g.getRawPointsFromLastDraw());
        return g.getBytes();
    }
    
    
    
    private final String[] createXLabels(int maxMonths) {
        final DateFormat df = new SimpleDateFormat("MMM yyyy"); //$NON-NLS-1$
        
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
            int maxMonths, PointSet left, PointSet right, PointSet averagePoints, 
            PointSet averageRank) {
        
        if (entries.size() < 2) {
            return null;
        }
        
        final DateFormat df = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
        
        final ScoreBoardEntry oldest = entries.get(0);
        
        final Date today = Time.currentTime();
        Point greatestLowerZeroPoints = null;
        Point lowestGreaterZeroPoints = null;
        
        Point greatestLowerZeroRank = null;
        Point lowestGreaterZeroRank = null;
        
        final ArrayDeque<Point> pointQueue = new ArrayDeque<>(AVERAGE_ELEMENTS);
        
        for (final ScoreBoardEntry entry : entries) {
            final int monthsAgo = this.getMonthsAgo(today, entry.getDate(), maxMonths);
            
            final double x = this.calcX(entry.getDate(), monthsAgo);
            final Point points = new NamedPoint(
                MSG.bind(MSG.scoreboardDatePoints, df.format(entry.getDate()), entry.getPoints()),
                x, entry.getPoints(), PointType.NONE);
            
            final Point rank = new NamedPoint(
                MSG.bind(MSG.scoreboardDateRank, df.format(entry.getDate()), entry.getPoints()),
                x, entry.getRank(), PointType.NONE);
            
            
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
        
        if (lowestGreaterZeroPoints != null && greatestLowerZeroPoints != null && Math.abs(
                    DateUtils.monthsBetween(today, oldest.getDate())) > maxMonths) {
            
            // interpolate correct y-axis intersection for points
            double m = (lowestGreaterZeroPoints.getY() - greatestLowerZeroPoints.getY()) / 
                    (lowestGreaterZeroPoints.getX() - greatestLowerZeroPoints.getX());
            double y = m * (-lowestGreaterZeroPoints.getX()) + lowestGreaterZeroPoints.getY();
            final Point zero = new Point(0.0, y, PointType.DOT); 
            left.add(zero);
            
            m = (lowestGreaterZeroRank.getY() - greatestLowerZeroRank.getY()) / 
                (lowestGreaterZeroRank.getX() - greatestLowerZeroRank.getX());
            y = m * (-lowestGreaterZeroRank.getX()) + lowestGreaterZeroRank.getY();
            right.add(new Point(0, y, PointType.DOT));
        }
        
        for (final Point p : left) {
            if (pointQueue.size() == AVERAGE_ELEMENTS) {
                Point avg = this.calcAverage(pointQueue, pointQueue.size());
                averagePoints.add(avg);
                pointQueue.poll();
            }
            pointQueue.add(p);
        }
        pointQueue.clear();
        for (final Point p : right) {
            if (pointQueue.size() == AVERAGE_ELEMENTS) {
                Point avg = this.calcAverage(pointQueue, pointQueue.size());
                averageRank.add(avg);
                pointQueue.poll();
            }
            pointQueue.add(p);
        }
        
        left.setName(oldest.getVenadName());
        right.setName(MSG.scoreboardRank);
        return lowestGreaterZeroPoints;
    }
    
    
    
    private Point calcAverage(Iterable<Point> iterable, int size) {
        double xsum = 0;
        double ysum = 0;
        for (Point p : iterable) {
            xsum = p.getX();
            ysum += p.getY();
        }
        return new Point(xsum, ysum / size, PointType.NONE);
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


    
    public synchronized void addEntries(final Collection<ScoreBoardEntry> entries) 
            throws DatabaseException {
        final List<ScoreBoardEntry> toAdd = new ArrayList<>(50);
        final Atomic op = new Atomic() {
            @Override
            public void perform(Write write) throws DatabaseException {
                final Read read = write.read();
                for (final ScoreBoardEntry entry : entries) {
                    final Collection<ScoreBoardEntry> existing = read.findList(
                            ScoreBoardEntry.class, ScoreBoardEntry.SBE_BY_USER,
                            new Param(entry.getVenadName()));
                    
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
                    write.all(toAdd);
                }
            }
        };
        
        this.persistence.writeAtomicParallel(op);
    }
    
    
    
    public void addEntry(final ScoreBoardEntry entry) throws DatabaseException {
        
        Collection<ScoreBoardEntry> existing = this.getEntries(entry.getVenadName());
        
        // skip identical entries.
        for (ScoreBoardEntry e : existing) {
            if (DateUtils.isSameDay(e.getDate(), entry.getDate())) {
                return;
            }
        }
        
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                write.single(entry);
            }
        });
    }
    
    
    
    public void deleteEntry(final int id) throws DatabaseException {
        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) throws DatabaseException {
                final ScoreBoardEntry sbe = write.read().find(ScoreBoardEntry.class, id);
                if (sbe == null) {
                    return;
                }
                write.remove(sbe);
            }
        });
    }
    
    
    
    
    public List<ScoreBoardEntry> getEntries(String venad) {
        return this.persistence.atomic().findList(ScoreBoardEntry.class, 
            ScoreBoardEntry.SBE_BY_USER, new Param(venad));
    }
    
    
    
    public List<ScoreBoardEntry> getEntries() {
        return this.persistence.atomic().findList(ScoreBoardEntry.class, 
            ScoreBoardEntry.ALL_SBE_DISTINCT);
    }
}
