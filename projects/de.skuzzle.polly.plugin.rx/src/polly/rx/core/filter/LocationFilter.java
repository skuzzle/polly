package polly.rx.core.filter;


import polly.rx.entities.BattleReport;


public class LocationFilter extends BattleReportFilter {

    private String location;
    
    
    public LocationFilter(String location) {
        this.location = location;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getQuadrant().equals(this.location);
    }
    
    

    @Override
    public String toString() {
        return "Location is: " + this.location;
    }

    
    
    public String getHint() {
        return "Matches only reports from the given location (quadrant only).";
    };
}
