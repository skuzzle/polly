package polly.rx.core.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import polly.rx.entities.BattleReport;

public class BattleReportFilterRunner {

    
    public final static List<BattleReport> filter(List<BattleReport> reports, 
            BattleReportFilter filter, BattleReportAggregator agg) {
        
        List<BattleReport> result = new ArrayList<BattleReport>(reports.size());
        for (BattleReport report : reports) {
            agg.process(report);
            if (filter.filter(report)) {
                result.add(report);
            }
        }
        return result;
    }
    
    
    
    public final static void filterInPlace(List<BattleReport> reports, 
            BattleReportFilter filter, BattleReportAggregator agg) {
        
        Iterator<BattleReport> it = reports.iterator();
        while (it.hasNext()) {
            BattleReport report = it.next();
            agg.process(report);
            if (!filter.filter(report)) {
                it.remove();
            }
        }
    }
        
}