package polly.rx.core.orion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polly.rx.core.orion.Graph.LazyBuilder;
import polly.rx.core.orion.Graph.Path;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;
import polly.rx.entities.DBSector;
import polly.rx.entities.SectorType;


public class PathPlanner {
    
    private class UniverseBuilder implements LazyBuilder<Sector> {
        
        private final Set<Sector> done;
        private final int costQuadrant;
        private final int costSector;
        
        
        public UniverseBuilder(int costQuadrant, int costSector) {
            this.done = new HashSet<>();
            this.costQuadrant = costQuadrant;
            this.costSector = costSector;
        }
        
        

        @Override
        public void collectIncident(Graph<Sector> source, Sector data) {
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
        
        
        
        private void addNeighbour(Quadrant quad, int x, int y, double costs,
                Graph<Sector> graph, Sector sourceData) {
            if (x < 0 || x > quad.getMaxX() || y < 0 || y > quad.getMaxY()) {
                return;
            }
            final Sector neighbour = quad.getSector(x, y);
            if (neighbour.getType() != SectorType.NONE) {
                final Graph<Sector>.Node source = graph.getNode(sourceData);
                final Graph<Sector>.Node target = graph.getNode(neighbour, neighbour); 
                source.edgeTo(target, costs);
            }
        }
    }
    
    
    private final QuadrantProvider quadProvider;
    private final WormholeProvider holeProvider;
    private final Graph<Sector> graph;
    
    
    public PathPlanner(QuadrantProvider quadProvider, WormholeProvider holeProvider) {
        this.graph = new Graph<>();
        this.quadProvider = quadProvider;
        this.holeProvider = holeProvider;
    }
    
    
    
    public class UniversePath extends Path<Sector> {

        public UniversePath(Path<Sector> path) {
            super(path.getNodes(), path.getCosts());
        }
    }
    
    
    
    public UniversePath findShortestPath(Sector start, Sector target, 
            int costQuadrant, int costSector) {
        final LazyBuilder<Sector> builder = 
                new UniverseBuilder(costQuadrant, costSector);
        return new UniversePath(this.graph.findShortestPath(start, target, builder));
    }
}