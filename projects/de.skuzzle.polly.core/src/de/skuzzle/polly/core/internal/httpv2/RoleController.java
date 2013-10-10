package de.skuzzle.polly.core.internal.httpv2;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.skuzzle.polly.http.annotations.Get;
import de.skuzzle.polly.http.annotations.OnRegister;
import de.skuzzle.polly.http.api.AlternativeAnswerException;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class RoleController extends PollyController {

    private final static String ADMIN_CATEGORY_KEY = "roleAdminCategory"; //$NON-NLS-1$
    private final static String ROLE_NAME_KEY = "roleManagerPage"; //$NON-NLS-1$
    private final static String ROLE_DESC_KEY = "roleManagerDesc"; //$NON-NLS-1$
    
    public final static String PAGE_ROLE_MANAGER = "pages/roles"; //$NON-NLS-1$
    public final static String CONTENT_ROLE_MANAGER = "/http/view/roles.html"; //$NON-NLS-1$
    
    public RoleController(MyPolly myPolly) {
        super(myPolly);
    }
    
    

    @Override
    protected Controller createInstance() {
        return new RoleController(this.getMyPolly());
    }
    
    
    
    @Get(value = PAGE_ROLE_MANAGER, name = ROLE_NAME_KEY)
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        MSG.FAMILY,
        ADMIN_CATEGORY_KEY,
        ROLE_DESC_KEY,
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer roles() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext(CONTENT_ROLE_MANAGER);
        c.put("roles", this.getMyPolly().roles().getRoles()); //$NON-NLS-1$
        Set<String> allPerms = new TreeSet<String>();
        for (String role : this.getMyPolly().roles().getRoles()) {
            allPerms.addAll(this.getMyPolly().roles().getPermissions(role));
        }
        c.put("allPerms", allPerms); //$NON-NLS-1$
        return this.makeAnswer(c);
    }
}
