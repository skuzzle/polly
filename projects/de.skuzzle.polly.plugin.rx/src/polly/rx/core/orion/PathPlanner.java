package polly.rx.core.orion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import polly.rx.core.orion.Graph.LazyBuilder;
import polly.rx.core.orion.Graph.Path;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.core.orion.model.Wormhole;


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
        public void collectIncident(Graph<Sector> graph, Sector source) {
            if (this.done.add(source)) {
                // add wormhole edges
                final Collection<Wormhole> holes = holeProvider.getWormholes(
                        source, quadProvider);
                
                for (final Wormhole hole : holes) {
                    final Quadrant targetQuad = quadProvider.getQuadrant(
                            hole.getTarget());
                    
                    this.addNeighbour(targetQuad, hole.getTarget().getX(), 
                            hole.getTarget().getY(), costQuadrant, graph, source);
                }
                
                // add direct neighbours
                final int x = source.getX();
                final int y = source.getY();
                final Quadrant quad = quadProvider.getQuadrant(source);
                for (int i = -1; i < 2; ++i) {
                    for (int j = -1; j < 2; ++j) {
                        this.addNeighbour(quad, x + i, y + j, costSector, graph, source);
                    }
                }
            }
        }
        
        
        
        private void addNeighbour(Quadrant quad, int x, int y, double costs,
                Graph<Sector> graph, Sector source) {
            if (x < 0 || x > quad.getMaxX() || y < 0 || y > quad.getMaxY()) {
                return;
            }
            final Sector neighbour = quad.getSector(x, y);
            if (neighbour.getType() != SectorType.NONE) {
                final Graph<Sector>.Node vSource = graph.getNode(source);
                final Graph<Sector>.Node vTarget = graph.getNode(neighbour, neighbour); 
                vSource.edgeTo(vTarget, costs);
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