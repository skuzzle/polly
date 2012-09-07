package polly.rx.core.filter;

import java.util.Set;
import java.util.TreeSet;



public abstract class ChainedFilter extends BattleReportFilter {

    private Set<BattleReportFilter> chain;

    
    
    public ChainedFilter(Set<BattleReportFilter> chain) {
        this.chain = chain;
    }
    
    
    
    public ChainedFilter() {
        this(new TreeSet<BattleReportFilter>());
    }
    
    
    
    public ChainedFilter(ChainedFilter other) {
        this(other.getChain());
    }
    
    
    
    public Set<BattleReportFilter> getChain() {
        return this.chain;
    }
}