package polly.rx.core.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import polly.rx.entities.BattleReport;

public class BattleReportFilterRunner {

    
    public final static List<BattleReport> filter(List<BattleReport> reports, 
            BattleReportFilter filter) {
        
        List<BattleReport> result = new ArrayList<BattleReport>(reports.size());
        for (BattleReport report : reports) {
            if (filter.accept(report)) {
                result.add(report);
            }
        }
        return result;
    }
    
    
    
    public final static void filterInPlace(List<BattleReport> reports, 
            BattleReportFilter filter) {
        
        Iterator<BattleReport> it = reports.iterator();
        while (it.hasNext()) {
            BattleReport report = it.next();
            if (!filter.accept(report)) {
                it.remove();
            }
        }
    }
        
}