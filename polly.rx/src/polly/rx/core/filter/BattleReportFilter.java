package polly.rx.core.filter;

import polly.rx.entities.BattleReport;

public interface BattleReportFilter {

    public boolean accept(BattleReport report);
    
}