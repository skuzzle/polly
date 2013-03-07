package polly.rx.core.filter;

import polly.rx.entities.BattleReport;

public interface BattleReportAggregator {

    public void process(BattleReport report);
}