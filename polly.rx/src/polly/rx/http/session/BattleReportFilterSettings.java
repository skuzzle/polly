package polly.rx.http.session;


import java.util.Map.Entry;
import java.util.Set;

import polly.rx.core.filter.AndFilter;
import polly.rx.core.filter.ChainedFilter;
import polly.rx.core.filter.OrFilter;
import polly.rx.core.filter.BattleReportFilter;



public class BattleReportFilterSettings {
    
    public final static String DATE_FILTER = "DATE_FILTER";
    public final static String ATTACKER_FILTER = "ATTACKER_FILTER";
    public final static String DEFENDER_FILTER = "DEFENDER_FILTER";
    public final static String LOCATION_FILTER = "LOCATION_FILTER";
    public final static String HAS_ARTIFACT_FILTER = "HAS_ARTIFACT_FILTER";
    public static final String TACTIC_FILTER = "TACTIC_FILTER";
    public static final String ID_LIST_FILTER = "ID_LIST_FILTER";
    public static final String DEFENDER_CLAN_FILTER = "DEFENDER_CLAN_FILTER";
    public static final String ATTACKER_CLAN_FILTER = "ATTACKER_CLAN_FILTER";
    
    
    private boolean switchOnAlienAttack;
    private ChainedFilter filter;
    private String chainingMethod;
    
    
    
    public BattleReportFilterSettings() {
        this.filter = new AndFilter();
        this.switchOnAlienAttack = true;
        this.chainingMethod = "AND";
    }
    
    
    
    public void filterAnd() {
        this.filter = new AndFilter(this.filter);
        this.chainingMethod = "AND";
    }
    
    
    
    public void filterOr() {
        this.filter = new OrFilter(this.filter);
        this.chainingMethod = "OR";
    }
    
    
    
    public String getChainingMethod() {
        return this.chainingMethod;
    }
    
    
    
    public boolean isSwitchOnAlienAttack() {
        return this.switchOnAlienAttack;
    }
    
    
    
    public void setSwitchOnAlienAttack(boolean switchOnAlienAttack) {
        this.switchOnAlienAttack = switchOnAlienAttack;
    }
    
    
    
    public BattleReportFilter getFilter() {
        return this.filter;
    }
    
    
    
    public void addFilter(String key, BattleReportFilter filter) {
        this.filter.getChain().put(key, filter);
    }
    
    
    
    public Set<Entry<String, BattleReportFilter>> filterSet() {
        return this.filter.getChain().entrySet();
    }
    
    
    
    public void removeFilter(String key) {
        this.filter.getChain().remove(key);
    }
    
    
    
    public void clearAll() {
        this.filter.getChain().clear();
    }
}