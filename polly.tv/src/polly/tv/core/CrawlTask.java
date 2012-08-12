package polly.tv.core;

import java.util.List;

import polly.tv.entities.TVEntity;


public interface CrawlTask extends Runnable {

    public abstract String getURL();
    
    public abstract List<TVEntity> parseResults(String page);
}