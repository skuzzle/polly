package de.skuzzle.polly.sdk.http;

import de.skuzzle.polly.sdk.MyPolly;


/**
 * This is a simple abstract base HttpAction that requires admin permissions to be 
 * executed.
 * 
 * @author Simon
 * @since 0.9.1
 * @see HttpAction
 */
public abstract class AbstractAdminAction extends HttpAction {

    /**
     * Creates a new AbstractAdminAction and adds 
     * {@link HttpManager#HTTP_ADMIN_PERMISSION} as a required permission.
     * 
     * @param name The name of this action.
     * @param myPolly The current MyPolly instance.
     */
    public AbstractAdminAction(String name, MyPolly myPolly) {
        super(name, myPolly);
        this.requirePermission(HttpManager.HTTP_ADMIN_PERMISSION);
    }

}
