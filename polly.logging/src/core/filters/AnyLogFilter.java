package core.filters;

import entities.LogEntry;


public class AnyLogFilter implements LogFilter {

    public boolean accept(LogEntry log) {
        return true;
    }
}