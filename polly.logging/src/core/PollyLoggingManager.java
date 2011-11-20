package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.filters.LogFilter;
import core.filters.SecurityLogFilter;
import core.output.IrcLogOutput;
import core.output.LogOutput;
import core.output.PasteServiceLogOutput;
import core.pasteservice.PasteServiceManager;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.model.User;
import entities.LogEntry;


public class PollyLoggingManager extends AbstractDisposable {
    
    private PersistenceManager persistence;
    private PasteServiceManager pasteServiceManager;
    
    private List<LogEntry> cache;
    private int cacheSize;
    private int pasteTreshold;
    private int maxLogs;
    
    
    
    public PollyLoggingManager(MyPolly myPolly, PasteServiceManager pasteServiceManager, 
                int cacheSize, int pasteTreshold, int maxLogs) {
        this.persistence = myPolly.persistence();
        this.pasteServiceManager = pasteServiceManager;
        
        this.cacheSize = cacheSize;
        this.pasteTreshold = pasteTreshold;
        this.maxLogs = maxLogs;
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
    
    
    
    public List<LogEntry> preFilterUser(String user) throws DatabaseException {
        return this.preFilterUser(user, this.maxLogs);
    }
    
    
    public List<LogEntry> preFilterUser(String user, int limit) throws DatabaseException {
        return this.preFilterQuery(LogEntry.FIND_BY_USER, limit, user);
    }
    
    
    
    public List<LogEntry> preFilterChannel(String channel) throws DatabaseException {
        return this.preFilterChannel(channel, this.maxLogs);
    }
    
    
    public List<LogEntry> preFilterChannel(String channel, int limit) throws DatabaseException {
        return this.preFilterQuery(LogEntry.FIND_BY_CHANNEL, limit, channel);
    }
    
    
    
    public LogEntry seenUser(String user) throws DatabaseException {
        return this.preFilterQuery(LogEntry.USER_SSEN, 1, user).get(0);
    }
    
    

    private List<LogEntry> preFilterQuery(String queryName, int limit, String parameter) 
            throws DatabaseException {
        this.storeCache();
        
        try {
            this.persistence.readLock();
            return this.persistence.findList(LogEntry.class, queryName, limit, 
                 new Object[] { parameter });
            
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    public void outputLogResults(MyPolly myPolly, User executer, List<LogEntry> logs, 
                String channel) {
        
        LogFormatter logFormatter = new DefaultLogFormatter();
        LogOutput output = null;
        
        // filter messages from channels that the executer is not on
        LogFilter security = new SecurityLogFilter(myPolly, executer);
        logs = this.postFilter(logs, security);
        
        // the results are sorted by date, newest item on top. Reverse the order so
        // the items are printed in proper order
        Collections.reverse(logs);
        
        if (logs.size() < this.pasteTreshold) {
            output = new IrcLogOutput();
        } else {
            output = new PasteServiceLogOutput(
                    this.pasteServiceManager.nextService());
        }
        
        output.outputLogs(myPolly.irc(), channel, logs, logFormatter, 
                myPolly.formatting());
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
                this.cache.clear();
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