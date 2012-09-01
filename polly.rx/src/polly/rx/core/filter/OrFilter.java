package polly.rx.core.filter;

import polly.rx.entities.BattleReport;


public class OrFilter  extends AbstractChainedFilter {

    
    @Override
    public boolean accept(BattleReport report) {
        for (BattleReportFilter filter : this.getChain()) {
            if (filter.accept(report)) {
                return true;
            }
        }
        return false;
    }
}
