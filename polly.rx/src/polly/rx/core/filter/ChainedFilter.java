package polly.rx.core.filter;

import java.util.Map;
import java.util.TreeMap;

import de.skuzzle.polly.sdk.FormatManager;



public abstract class ChainedFilter implements BattleReportFilter {

    private Map<String, BattleReportFilter> chain;

    
    
    public ChainedFilter(Map<String, BattleReportFilter> chain) {
        this.chain = chain;
    }
    
    
    
    public ChainedFilter() {
        this(new TreeMap<String, BattleReportFilter>());
    }
    
    
    
    public ChainedFilter(ChainedFilter other) {
        this(other.getChain());
    }
    
    
    
    public Map<String, BattleReportFilter> getChain() {
        return this.chain;
    }
    
    
    
    @Override
    public String toString(FormatManager formatManager) {
        return null;
    }
}