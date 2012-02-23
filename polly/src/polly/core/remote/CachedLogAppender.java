package polly.core.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import polly.network.protocol.LogItem;



public class CachedLogAppender extends AppenderSkeleton {

    private AdministrationManager adminManager;
    private int cacheSize;
    private List<LogItem> cache;
    private boolean enabled;
    
    
    public CachedLogAppender(AdministrationManager adminManager, int cacheSize) {
        this.cacheSize = cacheSize;
        this.adminManager = adminManager;
        this.cache = new ArrayList<LogItem>(cacheSize);
    }
    
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    
    
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    
    @Override
    public void close() {}

    
    
    @Override
    public boolean requiresLayout() {
        return false;
    }
    
    
    
    public void processLogCache(boolean force) {
        if (cache.size() >= this.cacheSize || force) {
            this.adminManager.processLogCache(this.cache);
        }
    }

    
    
    @Override
    protected void append(LoggingEvent le) {
        if (!this.enabled) {
            return;
        }
        
        synchronized (this.cache) {
            this.cache.add(new LogItem(le.getTimeStamp(), le.getLevel().toString(), 
                le.getThreadName(), le.getLoggerName(), le.getMessage().toString()));
            this.processLogCache(false);
        }
    }
}
