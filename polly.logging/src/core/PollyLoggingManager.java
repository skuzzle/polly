package core;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import entities.LogEntry;


public class PollyLoggingManager extends AbstractDisposable {
    
    private PersistenceManager persistence;
    private List<LogEntry> cache;
    private int cacheSize;
    
    
    
    public PollyLoggingManager(MyPolly myPolly) {
        this.persistence = myPolly.persistence();
        
        // TODO: store size in config
        this.cacheSize = 100;
        this.cache = new ArrayList<LogEntry>(this.cacheSize);
    }
    
    
    
    public void logMessage(LogEntry entry) throws DatabaseException {
        synchronized (this.cache) {
            this.cache.add(entry);
            
            if (this.cache.size() == cacheSize) {
                this.storeCache();
            }
        }
    }
    
    
    
    public List<LogEntry> preFilterUser(String user) {
        return null;
    }
    
    
    
    public List<LogEntry> preFilterChannel(String channel) {
        return null;
    }
    
    
    public LogEntry lastJoin(String user, String channel) {
        return null;
    }
    
    
    
    
    
    public List<LogEntry> postFilter(List<LogEntry> logs, LogFilter filter) {
        List<LogEntry> result = new ArrayList<LogEntry>();
        for (LogEntry log : logs) {
            if (filter.accept(log)) {
                result.add(log);
            }
        }
        return result;
    }
    
    
    
    public void storeCache() throws DatabaseException {
        synchronized (this.cache) {
            if (this.cache.isEmpty()) {
                return;
            }
        }
        
        
        try {
            this.persistence.writeLock();
            this.persistence.startTransaction();
            synchronized (this.cache) {
                this.persistence.persistList(this.cache);
            }
            this.persistence.commitTransaction();
        } finally {
            this.persistence.writeUnlock();
        }
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        try {
            this.storeCache();
        } catch (DatabaseException e) {
            throw new DisposingException(e);
        }
    }
}