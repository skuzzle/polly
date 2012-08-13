package polly.tv.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;



public abstract class AbstractCrawlTask implements CrawlTask {
        
    private String url;
    private TVProgramIndexer indexer;
    private TVServiceProvider provider;
    
    
    
    public AbstractCrawlTask(TVProgramIndexer indexer, 
            TVServiceProvider provider, String url) {
        this.indexer = indexer;
        this.provider = provider;
        this.url = url;
    }
    
    
    @Override
    public TVServiceProvider getProvider() {
        return this.provider;
    }
    
    
    
    @Override
    public String getURL() {
        return this.url;
    }
    
    
    
    @Override
    public void run() {
        this.indexer.getLogger().debug("Starting next crawl task...");
        
        URLConnection c = null;
        BufferedReader r = null;
        try {
            URL u = new URL(this.url);
            c = u.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; " +
            		"Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 " +
            		"Firefox/3.6.2");
            c.setDoInput(true);
            
            r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder b = new StringBuilder();
            String line = null;
            
            while ((line = r.readLine()) != null) {
                b.append(line);
            }
            
            List<CrawlTask> subTasks = this.processPage(this.indexer, b.toString());
            
            if (!subTasks.isEmpty()) {
                this.indexer.getLogger().debug(this + " yielded " + 
                    subTasks.size() + " new tasks");
                this.indexer.scheduleNextTasks(subTasks);
            }
        } catch (Exception e) {
            this.indexer.reportCrawlError(this, e);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    this.indexer.reportCrawlError(this, e);
                }
            }
        }
    }
    
    
    
    @Override
    public String toString() {
        return "[CrawlTask for '" + this.getURL() + "']";
    }
}
