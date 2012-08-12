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
        
        
        
        public AbstractCrawlTask(TVProgramIndexer indexer, String url) {
            this.url = url;
        }
        
        
        
        @Override
        public String getURL() {
            return this.url;
        }
        
        
        
        @Override
        public void run() {
            URLConnection c = null;
            BufferedReader r = null;
            try {
                URL u = new URL(this.url);
                c = u.openConnection();
                c.setDoInput(true);
                
                r = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder b = new StringBuilder();
                String line = null;
                
                while ((line = r.readLine()) != null) {
                    b.append(line);
                }
                List<TVEntity> results = this.parseResults(b.toString());
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
}
