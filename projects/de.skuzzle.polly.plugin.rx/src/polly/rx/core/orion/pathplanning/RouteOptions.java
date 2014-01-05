package polly.rx.core.orion.pathplanning;

import java.util.List;

import polly.rx.core.orion.model.Sector;
import de.skuzzle.polly.sdk.Types.TimespanType;


public class RouteOptions {
    final TimespanType totalJumpTime;
    final TimespanType currentJumpTime;
    final int maxWaitSpotDistance;
    final List<Sector> personalPortals;
    
    public RouteOptions(TimespanType totalJumpTime, TimespanType currentJumpTime, 
            List<Sector> personalPortals) {
        this.totalJumpTime = totalJumpTime;
        this.currentJumpTime = currentJumpTime;
        this.maxWaitSpotDistance = 3;
        this.personalPortals = personalPortals;
    }
    
    public TimespanType getCurrentJumpTime() {
        return this.currentJumpTime;
    }
    
    public TimespanType getTotalJumpTime() {
        return this.totalJumpTime;
    }
}