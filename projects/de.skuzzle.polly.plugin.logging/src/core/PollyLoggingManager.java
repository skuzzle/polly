package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import core.filters.ChainedLogFilter;
import core.filters.LimitFilter;
import core.filters.LogFilter;
import core.filters.SecurityLogFilter;
import core.filters.UserRegexFilter;
import core.output.IrcLogOutput;
import core.output.LogOutput;
import core.output.PasteServiceLogOutput;
import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.paste.PasteServiceManager;
import entities.LogEntry;


public class PollyLoggingManager extends AbstractDisposable {
    
    private PersistenceManagerV2 persistence;
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
        return this.preFilterQuery(LogEntry.FIND_BY_USER, user);
    }
    
    
    
    public List<LogEntry> preFilterChannel(String channel) 
            throws DatabaseException {
        return this.preFilterQuery(LogEntry.FIND_BY_CHANNEL, channel);
    }
    
    
    
    public LogEntry seenUser(String user) throws DatabaseException {
        List<LogEntry> seen = this.preFilterQuery(LogEntry.USER_SEEN, 1, user);
        if (seen.isEmpty()) {
            return LogEntry.forUnknown(user);
        } else {
            return seen.get(0);
        }
    }

    
    
    
    public List<LogEntry> filterUserRegex(String userRegex) 
            throws DatabaseException {
        List<LogEntry> allEntries = this.preFilterQuery(LogEntry.ALL_LOG_ENTRIES);
        return this.postFilter(allEntries, new UserRegexFilter(userRegex));
    }
    
    
    
    public List<LogEntry> getAllEntries() {
        try {
            return this.preFilterQuery(LogEntry.ALL_LOG_ENTRIES);
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    

    private List<LogEntry> preFilterQuery(String queryName, String...parameter) 
            throws DatabaseException {
        this.storeCache();
        return this.persistence.atomic().findList(LogEntry.class, queryName,  
             new Param(parameter));
    }
    
    
    
    private List<LogEntry> preFilterQuery(String queryName, int limit, String...parameter) 
        throws DatabaseException {
        this.storeCache();
        return this.persistence.atomic().findList(LogEntry.class, queryName, limit,
            new Param(parameter));
}
    
    
    
    public void outputLogResults(MyPolly myPolly, User executer, List<LogEntry> logs, 
                String channel) {
        
        LogFormatter logFormatter = new DefaultLogFormatter();
        LogOutput output = null;
        
        // filter messages from channels that the executer is not on
        LogFilter security = new SecurityLogFilter(myPolly, executer);
        LogFilter limitFilter = new LimitFilter(this.maxLogs);
        LogFilter chained = new ChainedLogFilter(security, limitFilter);
        int unfilteredSize = logs.size();
        logs = this.postFilter(logs, chained);
        
        // the results are sorted by date, newest item on top. Reverse the order so
        // the items are printed in proper order
        Collections.reverse(logs);
        
        if (logs.size() < this.pasteTreshold) {
            output = new IrcLogOutput();
        } else {
            output = new PasteServiceLogOutput(
                    this.pasteServiceManager.getRandomService());
        }
        
        output.outputLogs(myPolly.irc(), channel, logs, unfilteredSize, logFormatter, 
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
        final List<LogEntry> cpy;
        synchronized (this.cache) {
            if (this.cache.isEmpty()) {
                return;
            }
            cpy = new ArrayList<>(this.cache);
            this.cache.clear();
        }

        this.persistence.writeAtomic(new Atomic() {
            @Override
            public void perform(Write write) {
                write.all(cpy);
            }
        });
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