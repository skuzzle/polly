package polly.linkexpander;

import polly.linkexpander.commands.LinkGrabberCommand;
import polly.linkexpander.core.LinkGrabberManager;
import polly.linkexpander.core.LinkGrabberMessageListener;
import polly.linkexpander.core.grabbers.RxLinkGrabber;
import polly.linkexpander.core.grabbers.YouTubeLinkGrabber;
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
    
    
    private LinkGrabberManager linkGrabberManager;
    private MessageListener linkGrabber;
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
            DuplicatedSignatureException {
        
        super(myPolly);
        
        this.linkGrabberManager = new LinkGrabberManager();
        this.linkGrabber = new LinkGrabberMessageListener(this.linkGrabberManager);
        
        this.addCommand(new LinkGrabberCommand(myPolly, this.linkGrabberManager));
        
        this.linkGrabberManager.addLinkGrabber(new YouTubeLinkGrabber());
        this.linkGrabberManager.addLinkGrabber(new RxLinkGrabber());
        
        myPolly.irc().addMessageListener(this.linkGrabber);
    }
    
    
    
    @Override
    public void assignPermissions(RoleManager roleManager)
            throws RoleException, DatabaseException {
        
        roleManager.assignPermission(RoleManager.ADMIN_ROLE, GRABBER_PERMISSION);
    }

    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        
        this.getMyPolly().irc().removeMessageListener(this.linkGrabber);
    }
}
