package de.skuzzle.polly.sdk.http;

import de.skuzzle.polly.sdk.MyPolly;


public abstract class AbstractAdminAction extends HttpAction {

    public AbstractAdminAction(String name, MyPolly myPolly) {
        super(name, myPolly);
        this.permissions.add(HttpManager.HTTP_ADMIN_PERMISSION);
    }

}
