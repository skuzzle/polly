package polly.tv.core;

import java.util.Date;
import java.util.List;


public interface TVServiceProvider {

    public abstract String getName();
    
    public abstract String getBaseURL();
    
    public abstract String resolveRelativeLink(String link);
    
    public List<String> getSupportedChannels();
    
    public CrawlTask getFutureCrawlTask(TVProgramIndexer indexer, String channel, 
            Date day);
}