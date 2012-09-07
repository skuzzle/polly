package polly.rx.core.filter;

import java.util.Map;

import polly.rx.entities.BattleReport;


public class AndFilter extends ChainedFilter {

    
    public AndFilter() {
        super();
    }

    
    
    public AndFilter(ChainedFilter other) {
        super(other);
    }

    
    
    public AndFilter(Map<String, BattleReportFilter> chain) {
        super(chain);
    }

    
    
    @Override
    public boolean accept(BattleReport report) {
        for (BattleReportFilter filter : this.getChain().values()) {
            if (!filter.accept(report)) {
                return false;
            }
        }
        return true;
    }
    
    
    
    
    @Override
    public String getHint() {
        return "";
    }
}
