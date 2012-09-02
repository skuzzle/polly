package polly.rx.core.filter;

import java.util.Map;

import polly.rx.entities.BattleReport;


public class OrFilter  extends ChainedFilter {

    
    
    public OrFilter() {
        super();
    }

    
    
    public OrFilter(Map<String, BattleReportFilter> chain) {
        super(chain);
    }

    
    
    public OrFilter(ChainedFilter other) {
        super(other);
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        if (this.getChain().isEmpty()) {
            return true;
        }
        for (BattleReportFilter filter : this.getChain().values()) {
            if (filter.accept(report)) {
                return true;
            }
        }
        return false;
    }
}
