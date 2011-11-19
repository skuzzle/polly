package core.filters;

import java.util.Date;

import entities.LogEntry;


public class DateLogFilter implements LogFilter {
    
    private Date oldest;
    private Date newest;
    
    
    
    public DateLogFilter(Date oldest, Date newest) {
        this.oldest = oldest;
        this.newest = newest;
    }
    
    
    public DateLogFilter(Date oldest) {
        this(oldest, new Date());
    }
    
    

    @Override
    public boolean accept(LogEntry log) {
        long d = log.getDate().getTime();
        
        return d >= this.oldest.getTime() && d <= this.newest.getTime();
    }

}
