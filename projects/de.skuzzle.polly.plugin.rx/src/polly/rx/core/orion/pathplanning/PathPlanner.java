package polly.rx.core.orion.pathplanning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.QuadrantUtils;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.QuadrantDecorator;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorDecorator;
import polly.rx.core.orion.model.SectorType;
import polly.rx.core.orion.model.Wormhole;
import polly.rx.core.orion.pathplanning.Graph.EdgeCosts;
import polly.rx.core.orion.pathplanning.Graph.LazyBuilder;


public class PathPlanner {
    
    
    private final static Comparator<Sector> SAFE_SPOT_COMP = new Comparator<Sector>() {
        @Override
        public int compare(Sector o1, Sector o2) {
            // attacker bonus: the less, the better
            int c = Integer.compare(o1.getAttackerBonus(), o2.getAttackerBonus());
            if (c == 0) {
                // defender bonus: the more, the better
                c = Integer.compare(o2.getDefenderBonus(), o1.getDefenderBonus());
            }
            return c;
        }
    };
    
    
    
    private final static EdgeCosts<EdgeData> EDGE_COSTS = new PathCostCalculator();
    
    
    public static final int MAX_SAFE_SPOT_OUTPUT = 2;
    
    private final QuadrantProvider quadProvider;
    
    
    public PathPlanner(QuadrantProvider quadProvider, WormholeProvider holeProvider) {
        this.quadProvider = quadProvider;
    }
    
    
    
    private final static class HighlightedSector extends SectorDecorator {

        private final SectorType highlight;
        
        public HighlightedSector(Sector wrapped, SectorType highlight) {
            super(wrapped);
            this.highlight = highlight;
        }

        @Override
        public SectorType getType() {
            return this.highlight;
        }
    }
    
    
    
    public final static class HighlightedQuadrant extends QuadrantDecorator {
        
        private static int IDS = 0;
        
        private final Map<String, SectorType> highlights;
        private final int id;
        
        public HighlightedQuadrant(Quadrant wrapped) {
            super(wrapped);
            this.highlights = new HashMap<>();
            this.id = IDS++;
        }
        
        public int getId() {
            return this.id;
        }
        
        public void highlight(Sector sector, SectorType type) {
            this.highlight(sector.getX(), sector.getY(), type);
        }

        public void highlight(int x, int y, SectorType type) {
            final String key = x + "_" + y; //$NON-NLS-1$
            SectorType st = this.highlights.get(key);
            if (st == null) {
                this.highlights.put(key, type);
            } else {
                // highlight for this sector already exists, keep the one with higher id
                if (st.getId() < type.getId()) {
                    this.highlights.put(key, type);
                }
            }
        }
        
        @Override
        public Sector getSector(int x, int y) {
            final String key = QuadrantUtils.createMapKey(x, y);
            final Sector sector = super.getSector(x, y);
            final SectorType type = this.highlights.get(key);
            if (type != null) {
                return new HighlightedSector(sector, type);
            } else if (sector.getType() != SectorType.NONE){
                // render all unrelated sectors dark
                return new HighlightedSector(sector, SectorType.UNKNOWN);
            }
            return sector;
        }
    }
    
    
    
    public final static class Group {
        final static AtomicInteger IDS = new AtomicInteger();
        
        final int id;
        final List<Graph<Sector, EdgeData>.Edge> edges;
        final HighlightedQuadrant quad;
        
        private Group(Quadrant quadrant) {
            super();
            this.id = IDS.incrementAndGet();
            this.quad = new HighlightedQuadrant(quadrant);
            this.edges = new ArrayList<>();
        }
        
        public int getId() {
            return this.id;
        }
        
        public List<Graph<Sector, EdgeData>.Edge> getEdges() {
            return this.edges;
        }
        
        public Quadrant getQuadrant() {
            return this.quad;
        }
        
        public String getQuadName() {
            return this.quad.getName();
        }
    }
    
    
    
    public class UniversePath {
        
        private final Graph<Sector, EdgeData>.Path path;
        private final List<Group> groups;
        private final List<Wormhole> wormholes;
        private final int sectorJumps;
        private final int quadJumps;
        private final int minUnload;
        private final int maxUnload;
        private final int maxWaitTime;
        private final int sumMinWaitingTime;
        private final int sumMaxWaitingTime;
        private final RouteOptions options;
        
        /** Number used during path planning to block wormholes */
        private int blockNr; 
        /** States used during path planning to block wormholes */
        private boolean done;
        
        
        
        private UniversePath(Graph<Sector, EdgeData>.Path path, RouteOptions options) {
            this.path = path;
            this.options = options;
            this.groups = new ArrayList<>();
            this.wormholes = new ArrayList<>();

            String lastQuad = ""; //$NON-NLS-1$
            Group currentGroup = null;
            
            // always consider to be unloaded
            final int jtMinutes = (int) (options.totalJumpTime.getSpan() / 60.0);
            final int cjtMinutes = (int) (options.currentJumpTime.getSpan() / 60.0);
            int currentMinUnload = cjtMinutes;
            int currentMaxUnload = cjtMinutes;
            
            int sumMinUnload = 0;
            int sumMaxUnload = 0;
            int sumMinWaitingTime = 0;
            int sumMaxWaitingTime = 0;
            
            int maximumWaitTime = 0;
            
            boolean first = true;
            Graph<Sector, EdgeData>.Edge lastEdge = null;
            final Iterator<Graph<Sector, EdgeData>.Edge> it = path.getPath().iterator();

            while (it.hasNext()) {
                final Graph<Sector, EdgeData>.Edge e = it.next();
                final Sector source = e.getSource().getData();
                SectorType highlight = SectorType.HIGHLIGHT_SECTOR;
                
                if (currentGroup == null || !source.getQuadName().equals(lastQuad)) {
                    final Quadrant quad = quadProvider.getQuadrant(source.getQuadName());
                    currentGroup = new Group(quad);
                    this.groups.add(currentGroup);
                }
                
                if (lastEdge != null && lastEdge.getData().isWormhole()) {
                    // if last edge was a WH, current source node is a WH drop
                    highlight = SectorType.HIGHLIGHT_WH_DROP;
                }
                currentGroup.edges.add(e);
                lastQuad = source.getQuadName();
                
                if (e.getData().isWormhole()) {
                    final Wormhole hole = e.getData().getWormhole();
                    sumMinUnload += hole.getMinUnload();
                    sumMaxUnload += hole.getMaxUnload();
                    this.wormholes.add(hole);
                    highlight = SectorType.HIGHLIGHT_WH_START;
                    
                    switch (hole.requiresLoad()) {
                    case FULL:
                        e.getData().wait = new TimeRange(currentMinUnload, currentMaxUnload);
                        
                        currentMinUnload = hole.getMinUnload();
                        currentMaxUnload = hole.getMaxUnload();
                        break;
                    case PARTIAL:
                        final int waitMin = Math.max((jtMinutes - currentMinUnload) - (jtMinutes - hole.getMaxUnload()), 0);
                        final int waitMax = Math.max((jtMinutes - currentMaxUnload) - (jtMinutes - hole.getMaxUnload()), 0);
                        e.getData().wait = new TimeRange(waitMax, waitMin);
                        
                        currentMinUnload += hole.getMinUnload() - e.getData().wait.getMin();
                        currentMaxUnload += hole.getMaxUnload() - e.getData().wait.getMax();
                        break;
                    case NONE:
                        currentMinUnload += hole.getMinUnload();
                        currentMaxUnload += hole.getMaxUnload();
                    default:
                    }
                    maximumWaitTime = Math.max(maximumWaitTime, e.getData().wait.getMax());

                    e.getData().unloadAfter = new TimeRange(currentMinUnload, currentMaxUnload);
                    
                    if (e.getData().mustWait()) {
                        sumMinWaitingTime += e.getData().wait.getMin();
                        sumMaxWaitingTime += e.getData().wait.getMax();
                        
                        // find good spots
                        final Quadrant quad = quadProvider.getQuadrant(source.getQuadName());
                        final List<Sector> spots = QuadrantUtils.getNearSectors(source, 
                                quad, options.maxWaitSpotDistance, QuadrantUtils.ACCEPT_ALL);
                        Collections.sort(spots, SAFE_SPOT_COMP);
                        final int bound = Math.min(spots.size(), MAX_SAFE_SPOT_OUTPUT);
                        for (int i = 0; i < bound; ++i) {
                            
                            final Sector safeSpot = spots.get(i);
                            e.getData().waitSpots.add(safeSpot);
                            
                            if (safeSpot.equals(e.getSource().getData())) {
                                currentGroup.quad.highlight(safeSpot, 
                                        SectorType.HIGHLIGHT_SAFE_SPOT_WL);
                            } else {
                                currentGroup.quad.highlight(safeSpot, 
                                    SectorType.HIGHLIGHT_SAFE_SPOT);
                            }
                        }
                    }
                }
                if (first) {
                    highlight = SectorType.HIGHLIGHT_START;
                }
                currentGroup.quad.highlight(e.getSource().getData(), highlight);
                first = false;
                lastEdge = e;
            }
            // lastEdge is null if no way was found
            if (lastEdge != null) {
                currentGroup.quad.highlight(lastEdge.getTarget().getData(), 
                    SectorType.HIGHLIGHT_TARGET);
            }
            this.quadJumps = wormholes.size();
            this.sectorJumps = path.getPath().size() - this.quadJumps;
            this.minUnload = sumMinUnload;
            this.maxUnload = sumMaxUnload;
            this.maxWaitTime = maximumWaitTime;
            this.sumMinWaitingTime = sumMinWaitingTime;
            this.sumMaxWaitingTime = sumMaxWaitingTime;
        }
        
        public Wormhole getWormholeToBlock() {
            if (this.wormholes.isEmpty()) {
                return null;
            }
            int i = 0;
            for (final Wormhole hole : this.wormholes) {
                if (!hole.getName().equals(SectorType.EINTRITTS_PORTAL.toString())) {
                    if (i++ == this.blockNr) {
                        ++this.blockNr;
                        return hole;
                    }
                }
            }
            return null;
        }
        
        public int getMaxSafeSpotDistance() {
            return options.maxWaitSpotDistance;
        }
        
        public int getMaxWaitingTime() {
            return this.maxWaitTime;
        }
        
        public boolean pathFound() {
            return !this.path.getPath().isEmpty();
        }
        
        public int getMaxUnload() {
            return this.maxUnload;
        }
        
        public int getMinUnload() {
            return this.minUnload;
        }
        
        public int getSumMinWaitingTime() {
            return this.sumMinWaitingTime;
        }
        
        public int getSumMaxWaitingTime() {
            return this.sumMaxWaitingTime;
        }
        
        public int getSectorJumps() {
            return this.sectorJumps;
        }
        
        public List<Wormhole> getWormholes() {
            return this.wormholes;
        }
        
        public int getQuadJumps() {
            return this.quadJumps;
        }
        
        public List<Group> getGroups() {
            return this.groups;
        }
    }
    
    
    
    
    private UniversePath findShortestPath(Sector start, Sector target, 
            LazyBuilder<Sector, EdgeData> builder, RouteOptions options) {
        final Graph<Sector, EdgeData> graph = new Graph<>();
        final Graph<Sector, EdgeData>.Path path = graph.findShortestPath(
                start, target, builder, Graph.<Sector>noHeuristic(), EDGE_COSTS);
        final UniversePath result = new UniversePath(path, options);
        return result;
    }
    
    
    
    public UniversePath findShortestPath(Sector start, Sector target, 
            RouteOptions options) {
        return this.findShortestPath(start, target, new UniverseBuilder(options), options);
    }
    
    
    
    public List<UniversePath> findShortestPaths(Sector start, Sector target, 
            RouteOptions options) {
        final int K = 10;
        final UniverseBuilder builder = new UniverseBuilder(options);
        final List<UniversePath> result = new ArrayList<>(K);
        
        
        final UniversePath shortest = this.findShortestPath(start, target, 
                builder, options);
        result.add(shortest);
        
        if (shortest.pathFound()) {
            int pathsDone = 0;
            int nextPathIdx = -1;
            while (result.size() < K && pathsDone != result.size()) {
                nextPathIdx = (nextPathIdx + 1) % result.size();
                final UniversePath nextPath = result.get(nextPathIdx);
                if (nextPath.done) continue;
                
                final Wormhole block = nextPath.getWormholeToBlock();
                
                if (block != null) {
                    builder.startOverAndBlock(block);
                    
                    final UniversePath path = this.findShortestPath(start, target, 
                            builder, options);
                    if (path.pathFound()) {
                        result.add(path);
                    } else {
                        // if no path found yet, there will be no path when blocking 
                        // further holes, so this one's done
                        nextPath.done = true;
                        pathsDone++;
                    }
                } else {
                    nextPath.done = true;
                    pathsDone++;
                }
            }
        }
        
        return result;
    }
}