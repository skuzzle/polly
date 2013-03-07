package polly.rx.core.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.entities.BattleReport;


public class IdListFilter extends BattleReportFilter {

    private Set<Integer> ids;
    
    
    public IdListFilter(Integer[] ids) {
        this.ids = new TreeSet<Integer>(Arrays.asList(ids));
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return this.ids.contains(report.getId());
    }
    
    

    @Override
    public String toString() {
        return "ID Selection";
    }
    
    
    
    @Override
    public String getHint() {
        return "Matches reports with the selected ids.";
    }
}
