package core.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.LogEntry;


public class ChainedLogFilter implements LogFilter {

    private List<LogFilter> filters;
    
    
    public ChainedLogFilter(List<LogFilter> filters) {
        this.filters = filters;
    }
    
    
    public ChainedLogFilter(LogFilter...filters) {
        this.filters = new ArrayList<LogFilter>(Arrays.asList(filters));
    }
    
    
    
    public void addFilter(LogFilter filter) {
        this.filters.add(filter);
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