package de.skuzzle.polly.core.internal.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.sdk.http.HttpSession;


public class FileResponseHandler extends AbstractResponseHandler {

    private final static Logger logger = Logger
        .getLogger(FileResponseHandler.class.getName());
    
    protected String prefix;
    
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
        
        if (!dest.exists() || dest.getName().toLowerCase().contains(".html")) {
            logger.warn("Requested file does not exist or request has been blocked");
            session.increaseErrorCounter();
            this.respond404(t);
        } else {
            this.respond(new FileInputStream(dest), t, session);
        }
    }
    
    
    
    private void respond404(HttpExchange t) throws IOException {
        t.sendResponseHeaders(404, 0);
        t.close();
    }
    
    
    
    protected void respond(InputStream in, HttpExchange t, HttpSession session) 
            throws IOException {
        OutputStream out = null;
        try {
            t.sendResponseHeaders(200, 0);
            out = t.getResponseBody();
            byte[] buffer = new byte[1024];
            int len = 0;
            
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                this.counter.updateUpload(len);
                session.updateUpload(len);
            }
        } finally {
            t.close();
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
