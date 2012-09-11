package polly.core.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.sdk.http.HttpSession;


public class FileResponseHandler extends AbstractResponseHandler {

    private final static Logger logger = Logger
        .getLogger(FileResponseHandler.class.getName());
    
    private String prefix;
    
    public FileResponseHandler(HttpManagerImpl webServer, TrafficCounter counter, 
            String prefix) {
        super(webServer, counter);
        this.prefix = prefix;
    }
    
    

    @Override
    protected void handleRequest(String requestUri, HttpSession session,
            HttpExchange t, boolean timedOut, boolean blocked) throws IOException {

        String path = requestUri.substring(this.prefix.length());
        logger.debug("Evaluated requested file to: " + path);
        File dest = this.webServer.getPage(path);
        this.respond(dest, t, session);
    }
    
    
    
    private void respond(File dest, HttpExchange t, HttpSession session) 
            throws IOException {
        
        FileInputStream inp = null;
        OutputStream out = null;
        try {
            if (dest != null && dest.exists()) {
                t.sendResponseHeaders(200, 0);
                out = t.getResponseBody();
                inp = new FileInputStream(dest);
                byte[] buffer = new byte[1024];
                int len = 0;
                
                while ((len = inp.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    this.counter.updateUpload(len);
                    session.updateUpload(len);
                }
            } else {
                t.sendResponseHeaders(404, 0);
            }
        } finally {
            t.close();
            if (inp != null) {
                inp.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

}
