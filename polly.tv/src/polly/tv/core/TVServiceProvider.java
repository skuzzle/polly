package polly.tv.core;

import java.util.List;


public interface TVServiceProvider {

    public abstract String getName();
    
    public abstract String getBaseURL();
    
    public List<String> getSupportedChannels();
    
    public List<CrawlTask> getFutureCrawlTasks(int future);
}