package polly.rx.core.orion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polly.rx.core.orion.Graph.LazyBuilder;
import polly.rx.core.orion.Graph.Path;
import polly.rx.entities.QuadSector;
import polly.rx.entities.SectorType;


public class Universe {
    
    private final Map<String, Quadrant> allQuads;
    private final QuadrantManager qManager;
    private final Graph<QuadSector> graph;
    private final int costSector;
    private final int costQuadrant;
    private LazyBuilder<QuadSector> builder;
    
    
    
    private class UniverseBuilder implements LazyBuilder<QuadSector> {

        private final Set<QuadSector> done;
        
        
        public UniverseBuilder() {
            this.done = new HashSet<>();
        }
        
        
        
        @Override
        public void collectIncident(Graph<QuadSector> source, QuadSector data) {
            if (this.done.add(data)) {
                // add wormhole edges
                final List<Wormhole> holes = qManager.getWormholes(data);
                for (final Wormhole hole : holes) {
                    final Quadrant targetQuad = allQuads.get(hole.getTarget().getQuadName());
                    addNeighbour(targetQuad, hole.getTarget().getX(), 
                            hole.getTarget().getY(), costQuadrant, source, data);
                }
                
                // add direct neighbours
                final int x = data.getX();
                final int y = data.getY();
                final Quadrant quad = allQuads.get(data.getQuadName());
                for (int i = -1; i < 2; ++i) {
                    for (int j = -1; j < 2; ++j) {
                        addNeighbour(quad, x + i, y + j, costSector, source, data);
                    }
                }
            }
        }
        
    }
    
    
    
    public Universe(Collection<Quadrant> allQuads, 
            QuadrantManager qManager) {
        this.qManager = qManager;
        this.builder = new UniverseBuilder();
        this.costSector = 1;
        this.costQuadrant = 2;
        this.graph = new Graph<>();
        this.allQuads = new HashMap<String, Quadrant>();
        for (final Quadrant quad : allQuads) {
            this.allQuads.put(quad.getName(), quad);
        }
    }
    
    
    
    public class UniversePath extends Path<QuadSector> {

        public UniversePath(Path<QuadSector> path) {
            super(path.getNodes(), path.getCosts());
        }
    }
    
    
    
    public UniversePath findShortestPath(QuadSector start, QuadSector target) {
        return new UniversePath(this.graph.findShortestPath(start, target, this.builder));
    }
    
    
    
    private void addNeighbour(Quadrant quad, int x, int y, double costs,
            Graph<QuadSector> graph, QuadSector sourceData) {
        if (x < 0 || x > quad.getMaxX() || y < 0 || y > quad.getMaxY()) {
            return;
        }
        final QuadSector neighbour = quad.getSector(x, y);
        if (neighbour.getType() != SectorType.NONE) {
            final Graph<QuadSector>.Node source = graph.getNode(sourceData);
            final Graph<QuadSector>.Node target = graph.getNode(neighbour, neighbour); 
            source.edgeTo(target, costs);
        }
    }
}