package polly.core.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;



public class CachedLogAppender extends AppenderSkeleton {

    private AdministrationManager adminManager;
    private int cacheSize;
    private List<LoggingEvent> cache;
    private boolean enabled;
    
    
    public CachedLogAppender(AdministrationManager adminManager, int cacheSize) {
        this.cacheSize = cacheSize;
        this.adminManager = adminManager;
        this.cache = new ArrayList<LoggingEvent>(cacheSize);
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
    
    
    
    public void processLogCache() {
        if (cache.size() >= this.cacheSize) {
            this.adminManager.processLogCache(this.cache);
        }
    }

    
    
    @Override
    protected void append(LoggingEvent le) {
        if (!this.enabled) {
            return;
        }
        
        synchronized (this.cache) {
            this.cache.add(le);
            this.processLogCache();
        }
    }
}
