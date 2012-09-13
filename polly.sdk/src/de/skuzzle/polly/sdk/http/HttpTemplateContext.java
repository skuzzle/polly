package de.skuzzle.polly.sdk.http;

import java.util.Map;
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
    
    
    
    public HttpTemplateContext(String template) {
        this.template = template;
        this.context = new TreeMap<String, Object>();
    }
    
    
    
    public HttpTemplateContext() {
        this("");
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