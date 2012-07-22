package de.skuzzle.polly.sdk.http;

import java.util.Set;
import java.util.TreeSet;

import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityObject;


public abstract class HttpAction implements SecurityObject {
    
    private String name;
    protected Set<String> permissions;
    
    
    
    public HttpAction(String name) {
        this.name = name;
        this.permissions = new TreeSet<String>();
        this.permissions.add(RoleManager.NONE_PERMISSION);
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    @Override
    public Set<String> getRequiredPermission() {
        return this.permissions;
    }
    
    
    
    public abstract void execute(HttpEvent e, HttpTemplateContext context);
}