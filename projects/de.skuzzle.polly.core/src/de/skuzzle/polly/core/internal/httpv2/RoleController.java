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

    
    
    public RoleController(MyPolly myPolly) {
        super(myPolly);
    }
    
    

    @Override
    protected Controller createInstance() {
        return new RoleController(this.getMyPolly());
    }
    
    
    
    @Get(value = "pages/rolws", name = "Role Manager")
    @OnRegister({
        WebinterfaceManager.ADD_MENU_ENTRY,
        "Admin",
        "Manage polly roles and permissions",
        RoleManager.ADMIN_PERMISSION
    })
    public HttpAnswer roles() throws AlternativeAnswerException {
        this.requirePermissions(RoleManager.ADMIN_PERMISSION);
        final Map<String, Object> c = this.createContext("/http/view/roles.html");
        c.put("roles", this.getMyPolly().roles().getRoles());
        Set<String> allPerms = new TreeSet<String>();
        for (String role : this.getMyPolly().roles().getRoles()) {
            allPerms.addAll(this.getMyPolly().roles().getPermissions(role));
        }
        c.put("allPerms", allPerms);
        return this.makeAnswer(c);
    }
}
