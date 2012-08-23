package de.skuzzle.polly.sdk.http;

import java.util.HashMap;


/**
 * A template context specifies information for rendering html templates. First, it 
 * contains the path to the template to use, and second it has a map of objects that can 
 * be accessed by the velovity template engine.
 * 
 * @author Simon
 * @since 0.9.1
 */
public class HttpTemplateContext extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private String template;
    
    
    
    public HttpTemplateContext(String template) {
        this.template = template;
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
}