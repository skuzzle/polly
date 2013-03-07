package core.filters;

import entities.LogEntry;


public class LimitFilter implements LogFilter {

    private final int maxLogs;
    private int accepted;
    
    
    
    public LimitFilter(int maxLogs) {
        this.maxLogs = maxLogs;
    }
    
    
    
    @Override
    public boolean accept(LogEntry log) {
        return this.accepted++ < this.maxLogs;
    }
}
