package de.skuzzle.polly.sdk.http;

import java.util.HashMap;


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