package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class LocationFilter implements BattleReportFilter {

    private String location;
    
    
    public LocationFilter(String location) {
        this.location = location;
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return report.getQuadrant().equals(this.location);
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Location is: " + this.location;
    }

    
    
    public String getHint() {
        return "Matches only reports from the given location (quadrant only).";
    };
}
