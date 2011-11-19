package core;

import java.util.Arrays;
import java.util.List;

import entities.LogEntry;


public class CascadingLogFilter implements LogFilter {

    private List<LogFilter> filters;
    
    
    public CascadingLogFilter(List<LogFilter> filters) {
        this.filters = filters;
    }
    
    
    public CascadingLogFilter(LogFilter...filters) {
        this.filters = Arrays.asList(filters);
    }
    
    
    
    @Override
    public boolean accept(LogEntry log) {
        boolean result = true;
        
        for (LogFilter filter : this.filters) {
            if (filter == this) {
                continue;
            }
            result &= filter.accept(log);
            if (!result) {
                return false;
            }
        }
        
        return true;
    }
}