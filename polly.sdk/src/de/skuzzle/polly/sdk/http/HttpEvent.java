package de.skuzzle.polly.sdk.http;

import java.util.HashMap;
import java.util.Map;



public class HttpEvent {

    private HttpManager source;
    private HttpSession session;
    private Map<String, HttpParameter> properties;
    private String requestUri;
    
    
    
    public HttpEvent(HttpManager source, HttpSession session, String requestUri) {
        this.source = source;
        this.session = session;
        this.requestUri = requestUri;
        this.properties = new HashMap<String, HttpParameter>();
    }
    
    
    
    
    public String getRequestUri() {
        return this.requestUri;
    }
    
    
    
    public String getProperty(String key) {
        HttpParameter val = this.properties.get(key);
        return val == null ? null : val.getValue();
    }
    
    
    
    public Map<String, HttpParameter> getProperties() {
        return this.properties;
    }
    
    
    
    public HttpManager getSource() {
        return this.source;
    }
    
    
    
    public HttpSession getSession() {
        return this.session;
    }
}
