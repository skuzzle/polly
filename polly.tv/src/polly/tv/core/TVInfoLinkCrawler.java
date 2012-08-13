package polly.tv.core;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TVInfoLinkCrawler extends AbstractCrawlTask {

    private final static Pattern LINK_PATTERN = Pattern.compile(
        "<div.+?class=['\"]PGL['\"][^>]*><a href=['\"]([^\"']+)", Pattern.DOTALL);
    
    private static final int LINK_GROUP = 1;
    
    private String channel;
    
    
    
    public TVInfoLinkCrawler(TVProgramIndexer indexer, TVServiceProvider provider, 
            String url, String channel) {
        super(indexer, provider, url);
        this.channel = channel;
    }
    
    

    @Override
    public List<CrawlTask> processPage(TVProgramIndexer indexer, String page) {
        Matcher m = LINK_PATTERN.matcher(page);
        List<CrawlTask> subTasks = new LinkedList<CrawlTask>();
        
        while (m.find()) {
            String link = new String(page.substring(m.start(LINK_GROUP), 
                m.end(LINK_GROUP)));
            
            indexer.getLogger().trace("Crawled new detail link: '" + link + "'");
            subTasks.add(
                new TVInfoCrawlTask(
                    indexer, 
                    this.getProvider(), 
                    this.getProvider().resolveRelativeLink(link), 
                    this.channel)
            );
        }
        
        return subTasks;
    }

}
