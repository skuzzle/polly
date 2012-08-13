package polly.tv.core;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.time.DateUtils;
import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;

import polly.tv.entities.TVEntity;



public class TVProgramIndexer extends AbstractDisposable implements Runnable {
    

    private ScheduledExecutorService indexService;
    private PersistenceManager persistence;
    private List<TVServiceProvider> provider;
    private int futureDays;
    private Logger logger;
    private Date lastRun;
    private boolean running;
    private ScheduledFuture<?> service;
    private long lastScheduled;
    
    
    
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
        this.indexService = Executors.newScheduledThreadPool(10, 
            new ThreadFactoryBuilder("TV_INDEX_SERVICE_%n%")
                .setPriority(Thread.MIN_PRIORITY));
        
        this.provider = new LinkedList<TVServiceProvider>();
        this.lastScheduled = System.currentTimeMillis();
    }
    
    
    
    public Logger getLogger() {
        return this.logger;
    }
    
    
    
    public void startIndexService() {
        if (!this.running) {
            logger.info("Starting Indexer Service");
            this.service = this.indexService.scheduleAtFixedRate(this, 0, 
                this.serviceInterval, TimeUnit.HOURS);
            this.running = true;
        }
    }
    
    
    
    public void stopIndexService() {
        if (this.running) {
            this.service.cancel(false);
            this.running = false;
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
    
    
    
    public synchronized void scheduleNextTasks(List<CrawlTask> tasks) {
        long offset = Math.abs(System.currentTimeMillis() - this.lastScheduled);
        
        int i = 1;
        for (CrawlTask task : tasks) {
            // milliseconds!
            long delay = offset + i++ * this.scheduleDelay * 1000;
            logger.trace("Next task schedulued for execution at " + 
                    new Date(System.currentTimeMillis() + delay));
            this.indexService.schedule(task, delay, TimeUnit.MILLISECONDS);
        }
        this.lastScheduled = System.currentTimeMillis() + offset + 
            tasks.size() * this.scheduleDelay * 1000;
    }
    
    
    
    private List<CrawlTask> generateNextTasks(TVServiceProvider provider) {
        List<CrawlTask> nextTasks = new LinkedList<CrawlTask>();
        logger.debug("Generating next tasks from provider '" + provider + "'");
        
        
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
        
        List<CrawlTask> tasks = new LinkedList<CrawlTask>();
        
        synchronized (this.provider) {
            logger.trace("Registered Providers: " + this.provider.size());
            
            for (TVServiceProvider provider : this.provider) {
                List<CrawlTask> nextTasks = this.generateNextTasks(provider);
                tasks.addAll(nextTasks);
            }
        }
        
        // this serves for not crawling all pages ordered by date
        Collections.shuffle(tasks);
        logger.trace("Scheduling " + tasks.size() + " new tasks");
        this.scheduleNextTasks(tasks);
        
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
            "' just finished.");
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