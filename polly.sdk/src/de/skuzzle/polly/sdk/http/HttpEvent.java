package de.skuzzle.polly.sdk.http;

import java.util.HashMap;
import java.util.Map;


/**
 * HttpEvents are raised whenever a user accesses a polly web page. It contains the
 * requested uri as well as access to the submitted POST or GET parameters. Note that
 * all parameters are stored in a single map and thus their names must be unique among
 * POST and GET parameters.
 * 
 * @author Simon
 * @since 0.9.1
 */
public class HttpEvent {

    private HttpManager source;
    private HttpSession session;
    private Map<String, HttpParameter> properties;
    private String requestUri;
    
    
    
    /**
     * Creates a new HttpEvent.
     * 
     * @param source The HttpManager instance.
     * @param session The current session of the user who raised this event.
     * @param requestUri The uri that was requested.
     */
    public HttpEvent(HttpManager source, HttpSession session, String requestUri) {
        this.source = source;
        this.session = session;
        this.requestUri = requestUri;
        this.properties = new HashMap<String, HttpParameter>();
    }
    
    
    
    
    /**
     * Gets the requested uri.
     * 
     * @return The uri.
     */
    public String getRequestUri() {
        return this.requestUri;
    }
    
    
    
    /**
     * Gets the string value of either a POST or a GET parameter. 
     * 
     * @param key The name of the parameter.
     * @return The string value of that parameter or <code>null</code> if there is no
     *          such parameter.
     */
    public String getProperty(String key) {
        HttpParameter val = this.properties.get(key);
        return val == null ? null : val.getValue();
    }
    
    
    
    /**
     * Gets the map that contains all GET and POST parameters.
     * 
     * @return The parameter map.
     */
    public Map<String, HttpParameter> getProperties() {
        return this.properties;
    }
    
    
    
    /**
     * Gets the Httpmanager instance.
     * 
     * @return The HttpManager instance.
     */
    public HttpManager getSource() {
        return this.source;
    }
    
    
    
    /**
     * Gets the session of the user who raised this event.
     * 
     * @return The HttpSession.
     */
    public HttpSession getSession() {
        return this.session;
    }
    
    
    
    /**
     * This method simply throws a new {@link HttpTemplateException} and exists for 
     * convenience when implementing {@link HttpAction#execute(HttpEvent)}.
     * 
     * @param heading Heading of the resulting error message page.
     * @param message The error message.
     * @throws HttpTemplateException Will be thrown with the information given above.
     */
    public void throwTemplateException(String heading, String message) 
            throws HttpTemplateException {
        throw new HttpTemplateException(heading, message, this.getSession());
    }
    
    
    
    /**
     * This method simply throws a new {@link HttpTemplateException} and exists for 
     * convenience when implementing {@link HttpAction#execute(HttpEvent)}.
     * 
     * @param cause Other exception that caused this exception to be thrown.
     * @throws HttpTemplateException Will be thrown with the information given above.
     */
    public void throwTemplateException(Throwable cause) throws HttpTemplateException {
        throw new HttpTemplateException(this.getSession(), cause);
    }
}
