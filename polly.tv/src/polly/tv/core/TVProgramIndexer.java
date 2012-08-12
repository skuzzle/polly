package polly.tv.core;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;

import polly.tv.entities.TVEntity;


public class TVProgramIndexer extends AbstractDisposable implements Runnable {
    

    private ScheduledExecutorService indexService;
    private PersistenceManager persistence;
    private List<TVServiceProvider> provider;
    private int lifetime;
    
    // seconds!!
    private int scheduleDelay;
    
    
    
    public TVProgramIndexer(PersistenceManager persistence, 
            int crawlInterval, int lifetime, int scheduleDelay) {
        this.persistence = persistence;
        this.lifetime = lifetime;
        this.scheduleDelay = scheduleDelay;
        
        this.indexService = Executors.newScheduledThreadPool(2);
        this.indexService.scheduleAtFixedRate(this, 0, crawlInterval, TimeUnit.HOURS);
    }
    
    
    
    @Override
    public void run() {
        synchronized (this.provider) {
            for (TVServiceProvider provider : this.provider) {
                List<CrawlTask> nextTasks = provider.getFutureCrawlTasks(this.lifetime);
                for (CrawlTask task : nextTasks) {
                    this.indexService.schedule(task, this.scheduleDelay, 
                        TimeUnit.SECONDS);
                }
            }
        }
    }
    
    
    
    public void registerProvider(TVServiceProvider provider) {
        synchronized (this.provider) {
            this.provider.add(provider);
        }
    }
    
    
    
    public void cleanDatabase() {
        
    }
    
    
    
    void reportResults(CrawlTask reporter, List<TVEntity> results) {
        try {
            this.persistence.atomicPersistList(results);
        } catch (DatabaseException e) {
            this.reportCrawlError(reporter, e);
        }
    }
    
    
    
    void reportCrawlError(CrawlTask reporter, Exception e) {
        
    }



    @Override
    protected void actualDispose() throws DisposingException {
        synchronized (this.provider) {
            this.provider.clear();
        }
        
        try {
            this.indexService.awaitTermination(5000, TimeUnit.MILLISECONDS);
            this.indexService.shutdown();
        } catch (Exception e) {
            throw new DisposingException(e);
        }
    }
}