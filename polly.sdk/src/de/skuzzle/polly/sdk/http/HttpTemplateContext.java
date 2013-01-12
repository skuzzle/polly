package de.skuzzle.polly.sdk.http;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * A template context specifies information for rendering html templates. First, it 
 * contains the path to the template to use, and second it has a map of objects that can 
 * be accessed by the velocity template engine.
 * 
 * @author Simon
 * @since 0.9.1
 */
public class HttpTemplateContext {

    private String template;
    private Map<String, Object> context;
    private Set<Cookie> cookies;
    
    
    
    public HttpTemplateContext(String template) {
        this.template = template;
        this.context = new TreeMap<String, Object>();
        this.cookies = new HashSet<Cookie>();
    }
    
    
    
    public HttpTemplateContext() {
        this("");
    }
    
    
    
    public Set<Cookie> getCookies() {
        return this.cookies;
    }
    
    
    
    public void setCookie(Cookie c) {
        this.cookies.add(c);
    }
    
    
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    
    
    public String getTemplate() {
        return this.template;
    }
    
    
    
    public Object put(String s, Object o) {
        return this.context.put(s, o);
    }
    
    
    
    public Map<String, Object> getMap() {
        return this.context;
    }
}