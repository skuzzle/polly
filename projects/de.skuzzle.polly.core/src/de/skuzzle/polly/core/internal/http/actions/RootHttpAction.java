package de.skuzzle.polly.core.internal.http.actions;

import de.skuzzle.polly.core.internal.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;


public class RootHttpAction extends HttpAction {

    
    public RootHttpAction(MyPolly myPolly) {
        super("/", myPolly);
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws InsufficientRightsException {
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_HOME);
        context.put("formatter", this.myPolly.formatting());
        context.put("started", this.myPolly.getStartTime());
        long uptime = Time.currentTimeMillis() - this.myPolly.getStartTime().getTime();
        context.put("uptime", uptime / 1000);
        
        final String action = e.getProperty("action");
        
        if (action != null && action.equals("shutdown")) {
            if (!this.myPolly.roles().hasPermission(e.getSession().getUser(), 
                    RoleManager.ADMIN_PERMISSION)) {
                throw new InsufficientRightsException();
            }
            
            myPolly.shutdownManager().shutdown();
        } else if (action != null && action.equals("restart")) {
            if (!this.myPolly.roles().hasPermission(e.getSession().getUser(), 
                    RoleManager.ADMIN_PERMISSION)) {
                throw new InsufficientRightsException();
            }
            
            myPolly.shutdownManager().restart();
        }
        
        return context;
    }
}
