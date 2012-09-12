package polly.core.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;


import com.sun.net.httpserver.HttpExchange;

import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpParameter;
import de.skuzzle.polly.sdk.http.HttpParameter.ParameterType;
import de.skuzzle.polly.sdk.http.HttpSession;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class ActionResponseHandler extends AbstractResponseHandler {
    
    private final static Logger logger = Logger.getLogger(ActionResponseHandler.class
        .getName());
    
    
    
    public ActionResponseHandler(HttpManagerImpl webServer, TrafficCounter counter) {
        super(webServer, counter);
    }
    
    
    
    private HttpTemplateContext executeAction(HttpExchange t, HttpSession session, 
            String uri) throws IOException, HttpTemplateException, InsufficientRightsException {
        // extract GET parameters
        Map<String, HttpParameter> parameters = new HashMap<String, HttpParameter>();
        if (uri.contains("?")) {
            String[] parts = uri.split("\\?");
            
            if (parts.length == 1) {
                session.increaseErrorCounter();
                throw new HttpTemplateException("Invalid parameters", 
                    "Supplied URI has no valid parameters", session);
            }
            this.parseParameters(parts[1], parameters, ParameterType.GET);
            uri = parts[0];
        }
        
        logger.trace("Evaluated URI: " + uri);
        
        // extract POST parameters
        if (t.getRequestMethod().equals("POST")) {
            this.parsePostParameters(t, parameters, session);
        }
        
        
        HttpEvent e = new HttpEvent(this.webServer, session, uri);
        e.getProperties().putAll(parameters);
        
        return this.webServer.executeAction(e);
    }
    
    

    @Override
    public void handleRequest(String requestUri, HttpSession session, 
            HttpExchange t, boolean timedOut, boolean blocked) throws IOException {
        
        HttpTemplateContext c = null;
        // kill the session if a user is logged in on it and it is expired
        if (timedOut) {
            c = this.webServer.errorTemplate("Session expired", 
                "Your session has automatically been killed due to inactivity. " +
                "Please login and try again.", session);
        } else if (blocked) {
            c = this.webServer.errorTemplate("Session blocked", 
                "Your session has automatically been blocked because it caused " +
                "too many internal execution errors. Your session will be automtically " +
                "unblocked after it expired.", session);
        } else {
            try {
                c = this.executeAction(t, session, requestUri);
            } catch (HttpTemplateException e1) {
                c = this.webServer.errorTemplate(
                    e1.getHeading(), e1.getMessage(), session);
                
                logger.warn("Template Exception: ", e1);
            } catch (InsufficientRightsException e1) {
                c = this.webServer.errorTemplate(
                    "Permission denied", 
                    "You have insufficient permissions to acces this page/action. " +
                    "Note: the following list may be incomplete!" +
                    "<br/><br/>Missing permission(s): " + 
                        e1.getObject().getRequiredPermission(), session);
            } catch (Exception e1) {
                session.increaseErrorCounter();
                StringWriter w = new StringWriter();
                PrintWriter pw = new PrintWriter(w);
                e1.printStackTrace(pw);
                logger.error("Exception while executing Action", e1);
                c = this.webServer.errorTemplate("Exception while executing action", 
                        w.getBuffer().toString(), session);
            }
        }
        
        if (c != null) {
            this.respond(c, t, session);
        } else {
            // there is no action for the given uri, so treat it as a file request
            session.increaseErrorCounter();
            c = this.webServer.errorTemplate("404 - Not Found", 
                "Your request could not be processed because the requested " +
                "page/action was not found.", session);
            this.respond(c, t, session);
        }
    }
    
    
    
    private void respond(HttpTemplateContext c, HttpExchange t, HttpSession session) 
                throws IOException {
        
        t.sendResponseHeaders(200, 0);
        OutputStream out = null;
        try {
            out = t.getResponseBody();
            this.generateTemplate(c, out, session);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while sending http response", e);
        } finally {
            t.close();
            out.close();
        }
    }
    
    
    
    private void generateTemplate(HttpTemplateContext c, OutputStream out, 
            HttpSession session) throws IOException {
        Velocity.init();
        VelocityContext context = new VelocityContext(c);
        
        File dest = new File(this.webServer.getTemplateRoot(), "index.html");
        Template template = Velocity.getTemplate(dest.getPath(), 
                this.webServer.getEncoding());
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        byte[] data = writer.toString().getBytes(Charset.forName(
            this.webServer.getEncoding()));
        
        // update traffic data
        this.counter.updateUpload(data.length);
        session.updateUpload(data.length);
        out.write(data);
    }
}
