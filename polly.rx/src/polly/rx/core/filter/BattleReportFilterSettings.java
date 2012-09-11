package polly.rx.core.filter;


import java.util.Iterator;
import java.util.Set;



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
    public static final String ANY_VENAD_FILTER = "ANY_VENAD_FILTER";
    
    
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
    
    
    
    public void addFilter(BattleReportFilter filter) {
        this.filter.getChain().add(filter);
    }
    
    
    
    public Set<BattleReportFilter> getFilters() {
        return this.filter.getChain();
    }
    
    
    
    public BattleReportFilter getFilter(int id) {
        for (BattleReportFilter filter : this.filter.getChain()) {
            if (filter.getId() == id) {
                return filter;
            }
        }
        return null;
    }
    
    
    
    public void removeFilter(int id) {
        Iterator<BattleReportFilter> it = this.filter.getChain().iterator();
        while (it.hasNext()) {
            if (it.next().getId() == id) {
                it.remove();
                return;
            }
        }
    }
    
    
    
    public void clearAll() {
        this.filter.getChain().clear();
    }
}