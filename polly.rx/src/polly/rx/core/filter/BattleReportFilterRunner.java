package polly.rx.core.filter;

import java.util.ArrayList;
import java.util.List;
import polly.rx.entities.BattleReport;

public class BattleReportFilterRunner {

    
    public List<BattleReport> filter(List<BattleReport> reports, 
            BattleReportFilter filter) {
        
        List<BattleReport> result = new ArrayList<BattleReport>(reports.size());
        for (BattleReport report : reports) {
            if (filter.accept(report)) {
                result.add(report);
            }
        }
        return result;
    }
}