package polly.linkexpander;

import polly.linkexpander.commands.LinkGrabberCommand;
import polly.linkexpander.core.LinkGrabberManager;
import polly.linkexpander.core.LinkGrabberMessageListener;
import polly.linkexpander.core.grabbers.PhpBBLinkGrabber;
import polly.linkexpander.core.grabbers.URLLinkGrabber;
import polly.linkexpander.core.grabbers.YouTubeLinkGrabber;
import polly.linkexpander.http.GrabbedLinksHttpAction;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class MyPlugin extends PollyPlugin {

    public final static String GRABBER_PERMISSION = "polly.permission.LINK_GRABBER";
    public final static String URL_GRABBER_PERMISSION = "polly.permission.URL_GRABBER";
    
    
    private LinkGrabberManager linkGrabberManager;
    private MessageListener linkGrabber;
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            DuplicatedSignatureException {
        
        super(myPolly);
        
        this.linkGrabberManager = new LinkGrabberManager();
        this.linkGrabber = new LinkGrabberMessageListener(this.linkGrabberManager);
        myPolly.irc().addMessageListener(this.linkGrabber);
        
        this.addCommand(new LinkGrabberCommand(myPolly, this.linkGrabberManager));
        
        this.linkGrabberManager.addLinkGrabber(new YouTubeLinkGrabber());
        this.linkGrabberManager.addLinkGrabber(new PhpBBLinkGrabber());
        
        final URLLinkGrabber urlLinkGrabber = new URLLinkGrabber();
        this.linkGrabberManager.addLinkGrabber(urlLinkGrabber);
        
        myPolly.web().addHttpAction(new GrabbedLinksHttpAction(myPolly, urlLinkGrabber));
        myPolly.web().addMenuUrl("LinkGrabber", "Links");
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        
        roleManager.assignPermission(RoleManager.ADMIN_ROLE, GRABBER_PERMISSION);
        roleManager.assignPermission(RoleManager.ADMIN_PERMISSION, URL_GRABBER_PERMISSION);
    }

    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        
        this.getMyPolly().irc().removeMessageListener(this.linkGrabber);
    }
}
