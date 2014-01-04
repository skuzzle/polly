package polly.rx.core.orion.pathplanning;

import de.skuzzle.polly.sdk.Types.TimespanType;


public class RouteOptions {
    final TimespanType totalJumpTime;
    final TimespanType currentJumpTime;
    final int maxWaitSpotDistance;
    
    public RouteOptions(TimespanType totalJumpTime, TimespanType currentJumpTime) {
        this.totalJumpTime = totalJumpTime;
        this.currentJumpTime = currentJumpTime;
        this.maxWaitSpotDistance = 3;
    }
    
    public TimespanType getCurrentJumpTime() {
        return this.currentJumpTime;
    }
    
    public TimespanType getTotalJumpTime() {
        return this.totalJumpTime;
    }
}