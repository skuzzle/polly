package core;

import entities.LogEntry;


public interface LogFilter {

    public abstract boolean accept(LogEntry log);
}