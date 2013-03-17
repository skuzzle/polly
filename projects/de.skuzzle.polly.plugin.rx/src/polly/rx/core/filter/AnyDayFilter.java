package polly.rx.core.filter;

import java.util.Date;

import de.skuzzle.polly.sdk.time.DateUtils;

import polly.rx.entities.BattleReport;
import polly.rx.http.TemplateContextHelper;

public class AnyDayFilter extends BattleReportFilter {

    private Date base;
    
    public AnyDayFilter(Date base) {
        this.base = base;
    }
    
    
    
    @Override
    public String toString() {
        return "Same day as: " + TemplateContextHelper.getDateFormat().format(this.base);
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return DateUtils.isSameDay(this.base, report.getDate());
    }
    
    
    
    @Override
    public String getHint() {
        return "Matches only reports from the same day as the given date.";
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof BattleReportFilter)) {
            return false;
        } if (obj instanceof AnyDayFilter) {
            return DateUtils.isSameDay(this.base, ((AnyDayFilter)obj).base);
        } else {
            return this.getId() == ((BattleReportFilter) obj).getId();
        }
        
    }
}
