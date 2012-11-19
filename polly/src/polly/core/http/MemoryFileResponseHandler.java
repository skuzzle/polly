package polly.core.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.sdk.http.HttpSession;


public class MemoryFileResponseHandler extends FileResponseHandler {

    private final static Logger logger = Logger.getLogger(MemoryFileResponseHandler.class
        .getName());
    
    
    private Map<String, Byte[]> memoryFiles;
    
    
    
    public MemoryFileResponseHandler(HttpManagerImpl webServer, TrafficCounter counter, 
            Map<String, Byte[]> memoryFiles, String prefix) {
        super(webServer, counter, prefix);
        this.memoryFiles = memoryFiles;
    }
    
    

    @Override
    protected void handleRequest(String requestUri, HttpSession session, HttpExchange t,
            boolean timedOut, boolean blocked) throws IOException {
        
        String name = requestUri.substring(this.prefix.length());
        synchronized (this.memoryFiles) {
            Byte[] file = this.memoryFiles.get(name);
            
            if (file == null) {
                logger.warn("Requested memory file does not exist: " + name);
                session.increaseErrorCounter();
                t.sendResponseHeaders(404, 0);
                t.close();
            } else {
                byte[] data = new byte[file.length];
                for (int i = 0; i < file.length; ++i) {
                    data[i] = file[i];
                }
                this.respond(new ByteArrayInputStream(data), t, session);
            }
        }
    }
}
