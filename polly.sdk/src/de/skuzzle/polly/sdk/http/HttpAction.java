package de.skuzzle.polly.sdk.http;


public abstract class HttpAction {

    private String permission;
    private String name;
    
    
    
    public HttpAction(String name) {
        this(name, "");
    }
    
    
    
    public HttpAction(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    public String getPermission() {
        return this.permission;
    }
    
    
    
    public abstract void execute(HttpEvent e, HttpTemplateContext context);
    
}