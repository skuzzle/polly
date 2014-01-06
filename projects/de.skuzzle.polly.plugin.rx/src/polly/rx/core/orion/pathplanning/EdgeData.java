package polly.rx.core.orion.pathplanning;

import java.util.ArrayList;
import java.util.List;

import polly.rx.core.orion.model.EntryPortalWormhole;
import polly.rx.core.orion.model.Sector;
import polly.rx.core.orion.model.Wormhole;


public class EdgeData {
    
    public static enum EdgeType {
        NORMAL, DIAGONAL, WORMHOLE, ENTRYPORTAL;
    }

    
    public static EdgeData wormhole(Wormhole wormhole) {
        final EdgeData d = new EdgeData(EdgeType.WORMHOLE);
        d.wormhole = wormhole;
        return d;
    }
    
    public static EdgeData entryPortal(Sector source, Sector target) {
        final EdgeData d = new EdgeData(EdgeType.ENTRYPORTAL);
        d.wormhole = new EntryPortalWormhole(source, target);
        return d;
    }
    
    public static EdgeData sector(boolean diagonal) {
        if (diagonal) {
            return new EdgeData(EdgeType.DIAGONAL);
        }
        return new EdgeData(EdgeType.NORMAL);
    }
    
    
    final EdgeType type;
    Wormhole wormhole;
    int waitMin;
    int waitMax;
    final List<Sector> waitSpots;
    
    private EdgeData(EdgeType type) {
        this.type = type;
        this.waitSpots = new ArrayList<>(PathPlanner.MAX_SAFE_SPOT_OUTPUT);
    }

    public EdgeType getType() {
        return this.type;
    }
    
    public List<Sector> getWaitSpots() {
        return this.waitSpots;
    }
    
    public boolean hasWaitSpots() {
        return !this.waitSpots.isEmpty();
    }
    
    public boolean isWormhole() {
        return this.type == EdgeType.WORMHOLE || this.type == EdgeType.ENTRYPORTAL;
    }
    
    public Wormhole getWormhole() {
        return this.wormhole;
    }
    
    public boolean mustWait() {
        return this.waitMin > 0;
    }
    
    public int getWaitMin() {
        return this.waitMin;
    }
    
    public int getWaitMax() {
        return this.waitMax;
    }
}