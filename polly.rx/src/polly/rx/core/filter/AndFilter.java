package polly.rx.core.filter;

import polly.rx.entities.BattleReport;


public class AndFilter extends AbstractChainedFilter {

    
    @Override
    public boolean accept(BattleReport report) {
        for (BattleReportFilter filter : this.getChain()) {
            if (!filter.accept(report)) {
                return false;
            }
        }
        return true;
    }
    
    

}
