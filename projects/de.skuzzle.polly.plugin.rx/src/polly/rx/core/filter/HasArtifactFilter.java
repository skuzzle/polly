package polly.rx.core.filter;

import polly.rx.entities.BattleReport;


public class HasArtifactFilter extends BattleReportFilter {

    @Override
    public boolean acceptReport(BattleReport report) {
        return report.hasArtifact();
    }
    
    

    @Override
    public String toString() {
        return "Dropped Artifact";
    }

    
    
    @Override
    public String getHint() {
        return "Matches reports that dropped an artifcat.";
    }
}