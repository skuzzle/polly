package polly.rx.core.orion.pathplanning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import polly.rx.core.orion.Graph;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.WormholeProvider;
import polly.rx.core.orion.Graph.LazyBuilder;
import polly.rx.core.orion.model.Quadrant;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.SectorType;
import polly.rx.core.orion.model.Wormhole;


public class UniverseBuilder implements LazyBuilder<Sector, EdgeData> {

    private final Set<Sector> done;
    private final Collection<Wormhole> block;
    private final RouteOptions options;
    private final QuadrantProvider quadProvider;
    private final WormholeProvider holeProvider;
    
    
    
    public UniverseBuilder(RouteOptions options) {
        this.quadProvider = Orion.INSTANCE.createQuadrantProvider();
        this.holeProvider = Orion.INSTANCE.createWormholeProvider();
        
        this.options = options;
        this.done = new HashSet<>();
        this.block = new ArrayList<>();
    }
    
    
    
    public void startOverAndBlock(Wormhole hole) {
        this.done.clear();
        this.block.add(hole);
    }
    


    @Override
    public void collectIncident(Graph<Sector, EdgeData> graph, Sector source) {
        if (this.done.add(source)) {
            // add wormhole edges
            final Collection<Wormhole> holes = holeProvider.getWormholes(
                    source, quadProvider);
            
            for (final Wormhole hole : holes) {
                if (this.block.contains(hole)) {
                    continue;
                }
                final Quadrant targetQuad = quadProvider.getQuadrant(
                        hole.getTarget());
                
                final EdgeData d = EdgeData.wormhole(hole);
                this.addNeighbour(targetQuad, hole.getTarget().getX(), 
                        hole.getTarget().getY(), graph, source, d);
            }
            
            // add entry portals
            for (final Sector portal : quadProvider.getEntryPortals()) {
                final EdgeData d = EdgeData.entryPortal(source, portal);
                final Quadrant targetQuad = quadProvider.getQuadrant(portal);
                this.addNeighbour(targetQuad, portal.getX(), 
                        portal.getY(), graph, source, d);
            }
            
            // add personal portals
            for (final Sector personal : this.options.personalPortals) {
                final EdgeData d = EdgeData.entryPortal(source, personal);
                final Quadrant targetQuad = quadProvider.getQuadrant(personal);
                this.addNeighbour(targetQuad, personal.getX(), 
                        personal.getY(), graph, source, d);
            }
            
            // add direct neighbours
            final int x = source.getX();
            final int y = source.getY();
            final Quadrant quad = quadProvider.getQuadrant(source);
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    final boolean diagonal = Math.abs(i) == 1 && Math.abs(j) == 1;
                    final EdgeData d = EdgeData.sector(diagonal);
                    this.addNeighbour(quad, x + i, y + j, graph, source, d);
                }
            }
        }
    }
    
    
    
    private void addNeighbour(Quadrant quad, int x, int y, 
            Graph<Sector, EdgeData> graph, Sector source, EdgeData edgeData) {
        if (x < 0 || x > quad.getMaxX() || y < 0 || y > quad.getMaxY() || 
                (x == source.getX() && y == source.getY())) {
            return;
        }
        final Sector neighbour = quad.getSector(x, y);
        if (neighbour.getType() != SectorType.NONE) {
            final Graph<Sector, EdgeData>.Node vSource = graph.getNode(source);
            final Graph<Sector, EdgeData>.Node vTarget = graph.getNode(neighbour, neighbour); 
            vSource.edgeTo(vTarget, edgeData);
        }
    }
}