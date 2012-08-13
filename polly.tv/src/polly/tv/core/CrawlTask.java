package polly.tv.core;

import java.util.List;



public interface CrawlTask extends Runnable {

    public abstract String getURL();
    
    public abstract TVServiceProvider getProvider();
    
    public abstract List<CrawlTask> processPage(TVProgramIndexer indexer, String page);
}