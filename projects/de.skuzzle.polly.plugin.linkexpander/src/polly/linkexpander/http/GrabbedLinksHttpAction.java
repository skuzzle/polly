package polly.linkexpander.http;

import java.util.ArrayList;
import java.util.Collection;

import polly.linkexpander.MyPlugin;
import polly.linkexpander.core.grabbers.URLLinkGrabber;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class GrabbedLinksHttpAction extends HttpAction {

    private final URLLinkGrabber grabber;
    
    
    public GrabbedLinksHttpAction(MyPolly myPolly, URLLinkGrabber grabber) {
        super("/Links", myPolly);
        this.grabber = grabber;
        this.requirePermission(MyPlugin.URL_GRABBER_PERMISSION);
        this.requirePermission(RoleManager.REGISTERED_PERMISSION);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        
        final HttpTemplateContext c = new HttpTemplateContext("pages/links.html");
        
        final Collection<MessageEvent> filtered = 
            new ArrayList<MessageEvent>(this.grabber.getUrls().size());
        
        boolean admin = this.myPolly.roles().hasPermission(e.getSession().getUser(), 
            RoleManager.ADMIN_PERMISSION);
        for (final MessageEvent me : this.grabber.getUrls()) {
            if (admin || 
                this.getMyPolly().irc().isOnChannel(me.getChannel(), 
                e.getSession().getUser().getCurrentNickName())) {
                
                filtered.add(me);
            }
        }
        c.put("links", filtered);
        
        return c;
    }

}
