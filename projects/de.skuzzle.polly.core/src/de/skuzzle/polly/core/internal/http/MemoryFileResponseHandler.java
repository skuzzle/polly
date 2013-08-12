package de.skuzzle.polly.core.internal.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.sdk.http.HttpSession;


public class MemoryFileResponseHandler extends FileResponseHandler {

    private final static Logger logger = Logger.getLogger(MemoryFileResponseHandler.class
        .getName());
    
    
    private Map<String, InputStream> memoryFiles;
    
    
    
    public MemoryFileResponseHandler(HttpManagerImpl webServer, TrafficCounter counter, 
            Map<String, InputStream> memoryFiles, String prefix) {
        super(webServer, counter, prefix);
        this.memoryFiles = memoryFiles;
    }
    
    

    @Override
    protected void handleRequest(String requestUri, HttpSession session, HttpExchange t,
            boolean timedOut, boolean blocked) throws IOException {
        
        String name = requestUri.substring(this.prefix.length());
        synchronized (this.memoryFiles) {
            InputStream in = this.memoryFiles.get(name);
            t.getResponseHeaders().put("Cache-Control", 
                Arrays.asList("no-store", "max-age=0"));
            t.getResponseHeaders().put("Expires", 
                Arrays.asList("Fri, 30 Oct 1998 14:19:41 GMT"));
            if (in == null) {
                logger.warn("Requested memory file does not exist: " + name);
                session.increaseErrorCounter();
                t.sendResponseHeaders(404, 0);
                t.close();
            } else {
                synchronized (in) {
                    try {
                        in.mark(Integer.MAX_VALUE);
                        this.respond(in, t, session);
                    } finally {
                        in.reset();
                    }
                }
            }
        }
    }
}
