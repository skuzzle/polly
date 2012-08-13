package polly.tv.core;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.time.DateUtils;

import polly.tv.entities.TVEntity;



public class TVProgramIndexer extends AbstractDisposable implements Runnable {
    

    private ScheduledExecutorService indexService;
    private PersistenceManager persistence;
    private List<TVServiceProvider> provider;
    private int futureDays;
    private Logger logger;
    private Date lastRun;
    private boolean running;
    
    
    // hours!!
    private int serviceInterval;
    
    
    
    // seconds!!
    private int scheduleDelay;
    
    
    
    public TVProgramIndexer(PersistenceManager persistence, Logger logger,
            int serviceInterval, int futureDays, int scheduleDelay) {
        this.persistence = persistence;
        this.logger = logger;
        this.serviceInterval = serviceInterval;
        this.futureDays = futureDays;
        this.scheduleDelay = scheduleDelay;
        
        this.indexService = Executors.newScheduledThreadPool(10);
        this.provider = new LinkedList<TVServiceProvider>();
    }
    
    
    
    public Logger getLogger() {
        return this.logger;
    }
    
    
    
    public void startIndexService() {
        if (!this.running) {
            logger.info("Starting Indexer Service");
            this.indexService.scheduleAtFixedRate(this, 0, this.serviceInterval, 
                TimeUnit.HOURS);
            this.running = true;
        }
    }
    
    
    
    private Date getLastIndexedDate(String channel) {
        try {
            this.persistence.readLock();
            List<TVEntity> r = this.persistence.findList(TVEntity.class, 
                TVEntity.LATEST_BY_CHANNEL, 1, new Object[] { channel });
            
            if (r.isEmpty()) {
                return DateUtils.getDayAhead(1);
            }
            return r.get(0).getStart();
        } catch (Exception e) {
            logger.error("Unexpected fuck up", e);
            return DateUtils.getDayAhead(1);
        } finally {
            this.persistence.readUnlock();
        }
    }
    
    
    
    private List<CrawlTask> generateNextTasks(TVServiceProvider provider) {
        List<CrawlTask> nextTasks = new LinkedList<CrawlTask>();
        logger.debug("Generating next task from provider '" + provider + "'");
        
        
        for (String channel : provider.getSupportedChannels()) {
            Date latest = this.getLastIndexedDate(channel);
            
            logger.trace("Latest entry for TV channel '" + channel + "': " + latest);
            
            // create tasks for the next 'futureDays' days
            for (int i = 0; i < this.futureDays; ++i) {
                Date next = DateUtils.getDayAhead(i);
                if (next.compareTo(latest) < 0) {
                    // this is already indexed, so nothing needs to be crawled
                    logger.trace("Skipped Date " + next + " for channel '" + channel + 
                        "'");
                    continue;
                }
                nextTasks.add(provider.getFutureCrawlTask(this, channel, next));
            }
        }
        logger.trace("Generated " + nextTasks.size() + " new crawl tasks");
        return nextTasks;
    }
    
    
    
    @Override
    public void run() {
        if (this.lastRun != null) {
            logger.info("Running TV Program Indexer. Last run was " + this.lastRun);
        } else {
            logger.info("Running TV Program Indexer the first time for " +
            		"the current session");
        }
        
        synchronized (this.provider) {
            logger.trace("Registered Providers: " + this.provider.size());
            
            for (TVServiceProvider provider : this.provider) {
                List<CrawlTask> nextTasks = this.generateNextTasks(provider);
                
                // this serves for not crawling all pages ordered by date
                Collections.shuffle(nextTasks);
                
                logger.trace("Scheduling all new tasks");
                int i = 1;
                for (CrawlTask task : nextTasks) {
                    this.indexService.schedule(task, (i++) * this.scheduleDelay, 
                        TimeUnit.SECONDS);
                }
            }
        }
        
        this.lastRun = new Date();
    }
    
    
    
    public void registerProvider(TVServiceProvider provider) {
        synchronized (this.provider) {
            this.provider.add(provider);
        }
    }
    
    
    
    public void cleanDatabase() {
        
    }
    
    
    
    void reportResults(CrawlTask reporter, List<TVEntity> results) {
        this.logger.debug("Crawl task for URL '" + reporter.getURL() + 
            "' just finished. Success: " + reporter.success() + ", result size: " + 
            reporter.resultSize());
        try {
            this.persistence.atomicPersistList(results);
        } catch (DatabaseException e) {
            this.reportCrawlError(reporter, e);
        }
    }
    
    
    
    void reportCrawlError(CrawlTask reporter, Exception e) {
        logger.error("Crawl task '" + reporter + "' reported an error", e);
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