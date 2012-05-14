package de.skuzzle.polly.sdk.http;


public abstract class AbstractHttpAction implements HttpAction {

    private String permission;
    private String name;
    
    
    
    public AbstractHttpAction(String name) {
        this(name, "");
    }
    
    
    
    public AbstractHttpAction(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }
    
    
    
    @Override
    public String getName() {
        return this.name;
    }
    
    
    
    public String getPermission() {
        return this.permission;
    }
    
}