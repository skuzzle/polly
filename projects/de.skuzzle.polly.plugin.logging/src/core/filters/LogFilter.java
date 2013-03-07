package core.filters;

import entities.LogEntry;


public interface LogFilter {

    public abstract boolean accept(LogEntry log);
}