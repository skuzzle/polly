package de.skuzzle.polly.sdk.http;

import java.util.Set;
import java.util.TreeSet;


import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.roles.SecurityObject;


/**
 * <p>HTTP action build the base of the polly webinterface system. Using its name it can 
 * be executed when concatenated to the web url. When a user calls an action, its 
 * {@link #execute(HttpEvent)} method is executed. The passed HttpEvenet contains
 * infos about the user who called the action.</p>
 * 
 * <p>Http Actions are also subject to pollys security system and can thus report a set
 * of required permissions in order to be executed.</p>
 * 
 * @author Simon
 * @since 0.9.1
 * @see HttpEvent
 * @see HttpSession
 */
public abstract class HttpAction implements SecurityObject {
    
    private String name;
    protected Set<String> permissions;
    protected MyPolly myPolly;
    
    
    /**
     * Creates a new HttpAction.
     * 
     * @param name The name of this action. Note: this name must always start with '/'
     * @param myPolly The MyPolly instance.
     * @throws IllegalArgumentException If the name does not start with an '/'.
     */
    public HttpAction(String name, MyPolly myPolly) {
        if (!name.startsWith("/")) {
            throw new IllegalArgumentException("Invalid HttpAction name: " + name + 
                ", HttpAction names must start with '/'");
        }
        this.name = name;
        this.permissions = new TreeSet<String>();
        this.permissions.add(RoleManager.NONE_PERMISSION);
        this.myPolly = myPolly;
    }
    
    
    
    /**
     * Gets the name of this HttpAction.
     * 
     * @return The name.
     */
    public String getName() {
        return this.name;
    }
    
    
    
    @Override
    public Set<String> getRequiredPermission() {
        return this.permissions;
    }
    
    
    
    /**
     * Gets the MyPolly instance that this action was created with.
     * 
     * @return The MyPolly instance.
     */
    public MyPolly getMyPolly() {
        return this.myPolly;
    }
    

    
    /**
     * Adds a permission to this action. Actions can only be executed by users with this
     * permissions.
     * @param permission The permission to add.
     */
    public void requirePermission(String permission) {
        this.permissions.add(permission);
    }
    
    
    
    /**
     * <p>This method is called by polly when a user requested this action to be executed.
     * Using the passed {@link HttpEvent} you can get get {@link HttpSession} instance
     * which contains useful information like the executing user.</p>
     * 
     * <p>This method is not called if the user does not own all the required permissions
     * for this action. Instead he gets displayed a proper error message.</p>
     * 
     * <p>This method must return a new {@link HttpTemplateContext} which contains 
     * information about the resulting webpage that will be displayed to the user.</p>
     * 
     * @param e The HttpEvent that caused this action to be executed.
     * @return A HttpTemplateContext that contains information about the resulting web 
     *          page.
     * @throws HttpTemplateException This can be thrown whenever your action fails to
     *          execute. In this case, polly will display an error page with the
     *          information from this exception.
     */
    public abstract HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException;
}