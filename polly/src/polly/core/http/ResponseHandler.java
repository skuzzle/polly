package polly.core.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpSession;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class ResponseHandler implements HttpHandler {
    
    private final static Logger logger = Logger.getLogger(ResponseHandler.class
        .getName());
    
    /**
     * Regex for splitting GET style parameters from the request uri
     */
    public final static Pattern GET_PARAMETERS = Pattern.compile(
        "(\\w+)=([^&]+)");
    
    
    private static void parseGetParameters(String in, Map<String, String> params) {
        Matcher m = GET_PARAMETERS.matcher(in);
        
        while (m.find()) {
            String key = in.substring(m.start(1), m.end(1));
            String value = in.substring(m.start(2), m.end(2));
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(key, value);
        }
    }
    
    
    
    private static void parsePostParameters(HttpExchange t, Map<String, String> result) 
                throws IOException {
        
        BufferedReader r = new BufferedReader(new InputStreamReader(t.getRequestBody()));
        String line = null;
        while ((line = r.readLine()) != null) {
            if (!line.equals("")) {
                parseGetParameters(line, result);
            }
        }
    }
    
    
    private SimpleWebServer webServer;
    private File templateRoot;
    
    public ResponseHandler(SimpleWebServer webServer) {
        this.webServer = webServer;
        this.templateRoot = new File("webinterface");
    }
    
    

    public void handle(HttpExchange t) throws IOException {
        
        HttpSession session = this.webServer.getSession(t.getRemoteAddress().getAddress());
        session.setLastAction(System.currentTimeMillis());
        
        // kill the session if a user is logged in on it and it is expired
        if (session.isLoggedIn() && session.getLastAction() - session.getStarted() > 
                    SimpleWebServer.SESSION_TIMEOUT) {
            
            this.webServer.closeSession(session);
            return;
        }
        
        String uri = t.getRequestURI().toString();
        logger.trace(session + " requested " + uri);
        
        
        // extract GET parameters
        Map<String, String> parameters = new HashMap<String, String>();
        if (uri.contains("?")) {
            String[] parts = uri.split("\\?");
            parseGetParameters(parts[1], parameters);
            uri = parts[0];
        }
        
        logger.trace("Evaluated URI: " + uri);
        
        if (t.getRequestMethod().equals("POST")) {
            parsePostParameters(t, parameters);
        }
        
        HttpEvent e = new HttpEvent(this.webServer, session, uri);
        e.getProperties().putAll(parameters);
        
        HttpTemplateContext c = this.webServer.executeAction(e);
        if (c == null) {
            // there is no action for the given uri, so treat it as a file request
            this.respond(uri, t);
        } else {
            this.respond(c, t);
        }
        t.close();
    }
    
    
    
    private void respond(String uri, HttpExchange t) throws IOException {
        File dest = new File(this.templateRoot, uri);
        if (dest.exists()) {
            t.sendResponseHeaders(200, 0);
            
            FileInputStream inp = null;
            OutputStream out = null;
            try {
                out = t.getResponseBody();
                inp = new FileInputStream(dest);
                byte[] buffer = new byte[1024];
                int len = 0;
                
                while ((len = inp.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            } finally {
                if (inp != null) {
                    inp.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
    }
    
    
    
    private void respond(HttpTemplateContext c, HttpExchange t) throws IOException {
        t.sendResponseHeaders(200, 0);
        OutputStream out = null;
        try {
            out = t.getResponseBody();
            this.generateTemplate(c, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    
    
    
    private void generateTemplate(HttpTemplateContext c, OutputStream out) throws IOException {
        Velocity.init();
        VelocityContext context = new VelocityContext(c);
        
        File dest = new File(this.templateRoot, "index.html");
        Template template = Velocity.getTemplate(dest.getPath());
        //OutputStreamWriter writer = new OutputStreamWriter(out);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.write(writer.toString().getBytes());
    }
}
