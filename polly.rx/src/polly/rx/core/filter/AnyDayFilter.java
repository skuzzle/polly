package polly.rx.core.filter;

import java.util.Date;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.time.DateUtils;

import polly.rx.entities.BattleReport;


public class AnyDayFilter implements BattleReportFilter {

    private Date base;
    
    public AnyDayFilter(Date base) {
        this.base = base;
    }
    
    
    
    @Override
    public String toString(FormatManager formatManager) {
        return "Same day as: " + formatManager.formatDate(this.base);
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return DateUtils.isSameDay(this.base, report.getDate());
    }
    
    
    public String getHint() {
        return "Matches only reports from the same day as the given date.";
    };
}
