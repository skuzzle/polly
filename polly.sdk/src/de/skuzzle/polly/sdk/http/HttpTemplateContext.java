package de.skuzzle.polly.sdk.http;

import java.util.HashMap;


public class HttpTemplateContext extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private String resultUrl;
    
    
    
    public HttpTemplateContext(String resultUrl) {
        this.resultUrl = resultUrl;
    }
    
    
    
    public HttpTemplateContext() {
        this("");
    }
    
    
    
    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }
    
    
    
    public String getResultUrl() {
        return this.resultUrl;
    }
    
}