package polly.rx.core.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class IdListFilter implements BattleReportFilter {

    private Set<Integer> ids;
    
    
    public IdListFilter(Integer[] ids) {
        this.ids = new TreeSet<Integer>(Arrays.asList(ids));
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return this.ids.contains(report.getId());
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "ID Selection";
    }
}
