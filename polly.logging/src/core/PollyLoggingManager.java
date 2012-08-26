package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import core.filters.LogFilter;
import core.filters.SecurityLogFilter;
import core.output.IrcLogOutput;
import core.output.LogOutput;
import core.output.PasteServiceLogOutput;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.paste.PasteServiceManager;
import entities.LogEntry;


public class PollyLoggingManager extends AbstractDisposable {
    
    private PersistenceManager persistence;
    private PasteServiceManager pasteServiceManager;
    
    private List<LogEntry> cache;
    private int cacheSize;
    private int pasteTreshold;
    private int maxLogs;
    
    
    
    public PollyLoggingManager(MyPolly myPolly, int cacheSize, int pasteTreshold, 
            int maxLogs) {
        this.persistence = myPolly.persistence();
        this.pasteServiceManager = myPolly.pasting();
        
        this.cacheSize = cacheSize;
        this.pasteTreshold = pasteTreshold;
        this.maxLogs = maxLogs;
        this.cache = new ArrayList<LogEntry>(this.cacheSize);
    }
    
    
    
    public void logMessage(LogEntry entry) throws DatabaseException {
        synchronized (this.cache) {
            String msg = entry.getMessage();
            
            do {
                LogEntry newEntry = new LogEntry(entry);
                int newLen = Math.min(LogEntry.MESSAGE_LEN, msg.length());
                newEntry.setMessage(msg.substring(0, newLen));
                msg = msg.substring(newLen);
                this.cache.add(newEntry);
            } while (msg.length() > 0);
            
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
    
    
    public List<LogEntry> preFilterChannel(String channel, int limit) 
            throws DatabaseException {
        return this.preFilterQuery(LogEntry.FIND_BY_CHANNEL, limit, channel);
    }
    
    
    
    public LogEntry seenUser(String user) throws DatabaseException {
        List<LogEntry> seen = this.preFilterQuery(LogEntry.USER_SSEN, 1, user);
        if (seen.isEmpty()) {
            return LogEntry.forUnknown(user);
        } else {
            return seen.get(0);
        }
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
                    this.pasteServiceManager.getRandomService());
        }
        
        output.outputLogs(myPolly.irc(), channel, logs, logFormatter, 
                myPolly.formatting());
        // clear log list
        logs.clear();
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
    
    
    
    public List<LogEntry> postFilter(List<LogEntry> logs, LogFilter filter, int max) {
        List<LogEntry> result = new ArrayList<LogEntry>(max);
        Iterator<LogEntry> it = logs.iterator();
        while (result.size() < max && it.hasNext()) {
            LogEntry log = it.next();
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

            this.persistence.atomicPersistList(this.cache);
            this.cache.clear();
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