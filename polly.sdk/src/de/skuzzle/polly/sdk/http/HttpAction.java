package de.skuzzle.polly.sdk.http;

import java.util.Set;
import java.util.TreeSet;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityObject;


public abstract class HttpAction implements SecurityObject {
    
    private String name;
    protected Set<String> permissions;
    protected MyPolly myPolly;
    
    
    
    public HttpAction(String name, MyPolly myPolly) {
        this.name = name;
        this.permissions = new TreeSet<String>();
        this.permissions.add(RoleManager.NONE_PERMISSION);
        this.myPolly = myPolly;
    }
    
    
    
    public String getName() {
        return this.name;
    }
    
    
    
    @Override
    public Set<String> getRequiredPermission() {
        return this.permissions;
    }
    
    
    
    public MyPolly getMyPolly() {
        return this.myPolly;
    }
    
    
    
    public abstract HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException;
}