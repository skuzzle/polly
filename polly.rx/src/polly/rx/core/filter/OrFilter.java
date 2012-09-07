package polly.rx.core.filter;

import java.util.Set;

import polly.rx.entities.BattleReport;


public class OrFilter  extends ChainedFilter {

    
    
    public OrFilter() {
        super();
    }

    
    
    public OrFilter(Set<BattleReportFilter> chain) {
        super(chain);
    }

    
    
    public OrFilter(ChainedFilter other) {
        super(other);
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        if (this.getChain().isEmpty()) {
            return true;
        }
        for (BattleReportFilter filter : this.getChain()) {
            if (filter.filter(report)) {
                return true;
            }
        }
        return false;
    }
    
    
    
    @Override
    public String getHint() {
        return "Chains a list of filters using an 'or' operation which means at least " +
        		"one of the filters must match in order to show a report.";
    }
}
