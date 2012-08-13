package polly.tv.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import polly.tv.entities.TVEntity;


public abstract class AbstractCrawlTask implements CrawlTask {
        
    private String url;
    private TVProgramIndexer indexer;
    private boolean success;
    private int resultSize;
    
    
    
    public AbstractCrawlTask(TVProgramIndexer indexer, String url) {
        this.indexer = indexer;
        this.url = url;
    }
    
    
    
    @Override
    public String getURL() {
        return this.url;
    }
    
    
    
    @Override
    public int resultSize() {
        return this.resultSize;
    }
    
    
    
    @Override
    public boolean success() {
        return this.success;
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
            List<TVEntity> results = this.parseResults(b.toString());
            this.success = true;
            this.resultSize = results.size();
            
            this.indexer.reportResults(this, results);
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
