package polly.rx.core.filter;

import de.skuzzle.polly.sdk.FormatManager;
import polly.rx.entities.BattleReport;

public interface BattleReportFilter {

    public boolean accept(BattleReport report);
    
    public abstract String toString(FormatManager formatManager);
    
    public String getHint();
}