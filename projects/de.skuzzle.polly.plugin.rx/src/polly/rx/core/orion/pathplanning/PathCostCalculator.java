package polly.rx.core.orion.pathplanning;

import polly.rx.core.orion.model.Wormhole;
import polly.rx.core.orion.pathplanning.Graph.EdgeCosts;


public class PathCostCalculator implements EdgeCosts<EdgeData> {
    
    
    private final double COST_DIAGONAL = 1.5 / 60;
    private final double COST_NORMAL = 1.0 / 60;
    private final double COST_ENTRYPORTAL = COST_DIAGONAL * 3.0;
    private final double WORMHOLE_OFFSET = 100000.0;
    
    
    @Override
    public double calculate(EdgeData data) {
        switch (data.getType()) {
        case NORMAL:   return COST_NORMAL;
        case DIAGONAL: return COST_DIAGONAL;
        case ENTRYPORTAL: return COST_ENTRYPORTAL;
        case WORMHOLE: 
            final Wormhole hole = data.getWormhole();
            double modifier = 1.0;
            switch (hole.requiresLoad()) {
            case FULL:
                modifier = 50.0;
                break;
            case PARTIAL:
                modifier = 10.0;
                break;
            case NONE:
            }
            return WORMHOLE_OFFSET + modifier * Math.max(1, data.getWormhole().getMinUnload());
        default: return Double.MAX_VALUE;
        }
    }
    
}