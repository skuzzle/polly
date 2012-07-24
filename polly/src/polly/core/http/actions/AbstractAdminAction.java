package polly.core.http.actions;

import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpManager;


public abstract class AbstractAdminAction extends HttpAction {

    public AbstractAdminAction(String name) {
        super(name);
        this.permissions.add(HttpManager.HTTP_ADMIN_PERMISSION);
    }

}
