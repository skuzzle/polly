package de.skuzzle.polly.sdk.http;

import java.util.HashMap;
import java.util.Map;



public class HttpEvent {

    private HttpManager source;
    private HttpSession session;
    private Map<String, String> properties;
    private String requestUri;
    
    
    
    public HttpEvent(HttpManager source, HttpSession session, String requestUri) {
        this.source = source;
        this.session = session;
        this.requestUri = requestUri;
        this.properties = new HashMap<String, String>();
    }
    
    
    
    
    public String getRequestUri() {
        return this.requestUri;
    }
    
    
    
    public String getProperty(String key) {
        return this.properties.get(key);
    }
    
    
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    
    
    
    public HttpManager getSource() {
        return this.source;
    }
    
    
    
    public HttpSession getSession() {
        return this.session;
    }
}
