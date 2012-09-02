package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class HasArtifactFilter implements BattleReportFilter {

    @Override
    public boolean accept(BattleReport report) {
        return report.hasArtifact();
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Dropped Artifact";
    }

}