package polly.rx.core.filter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



public abstract class AbstractChainedFilter implements BattleReportFilter {

    private List<BattleReportFilter> chain;

    
    public AbstractChainedFilter(BattleReportFilter...chain) {
        this.chain = Arrays.asList(chain);
    }
    
    
    
    public AbstractChainedFilter(List<BattleReportFilter> chain) {
        this.chain = chain;
    }
    
    
    
    public AbstractChainedFilter() {
        this(new LinkedList<BattleReportFilter>());
    }
    
    
    
    public List<BattleReportFilter> getChain() {
        return this.chain;
    }
}