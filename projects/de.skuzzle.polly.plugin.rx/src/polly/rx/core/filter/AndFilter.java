package polly.rx.core.filter;

import java.util.Set;

import polly.rx.entities.BattleReport;


public class AndFilter extends ChainedFilter {

    
    public AndFilter() {
        super();
    }

    
    
    public AndFilter(ChainedFilter other) {
        super(other);
    }

    
    
    public AndFilter(Set<BattleReportFilter> chain) {
        super(chain);
    }

    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        for (BattleReportFilter filter : this.getChain()) {
            if (!filter.filter(report)) {
                return false;
            }
        }
        return true;
    }
    
    
    
    
    @Override
    public String getHint() {
        return "Chains a list of filters using an 'and' operation which means that each" +
        		"filter must match in order to show a report.";
    }
}
