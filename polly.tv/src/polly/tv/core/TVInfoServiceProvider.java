package polly.tv.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;



public class TVInfoServiceProvider implements TVServiceProvider {

    private final static List<String> CHANNELS;
    static {
        CHANNELS = new LinkedList<String>();
        CHANNELS.add("pro7");
        CHANNELS.add("sat1");
    }
    
    
    private DateFormat df;
    
    
    public TVInfoServiceProvider() {
        this.df = new SimpleDateFormat("d.M.yyyy");
    }
    
    
    @Override
    public String getName() {
        return "TVInfoServiceProvider";
    }

    
    
    @Override
    public String getBaseURL() {
        return "http://www.tvinfo.de";
    }
    
    
    
    @Override
    public String resolveRelativeLink(String link) {
        return this.getBaseURL() + (link.startsWith("/") ? link : "/" + link);
    }
    
    

    @Override
    public List<String> getSupportedChannels() {
        return CHANNELS;
    }
    
    

    @Override
    public CrawlTask getFutureCrawlTask(TVProgramIndexer indexer, String channel, 
            Date day) {
        String url = this.createURL(channel, day);
        return new TVInfoLinkCrawler(indexer, this, url, channel);
    }
    
    
    
    private String createURL(String channel, Date day) {
        return this.resolveRelativeLink("/sender/" + channel + "/" + this.df.format(day));
    }
    
    
    
    @Override
    public String toString() {
        return "[TvServiceProvider '" + this.getName() + "']";
    }
}
