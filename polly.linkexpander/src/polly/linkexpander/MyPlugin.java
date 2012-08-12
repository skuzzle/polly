package polly.linkexpander;

import polly.linkexpander.core.LinkGrabberManager;
import polly.linkexpander.core.LinkGrabberMessageListener;
import polly.linkexpander.core.grabbers.YouTubeLinkGrabber;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {

    private LinkGrabberManager linkGrabberManager;
    private MessageListener linkGrabber;
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException {
        super(myPolly);
        
        this.linkGrabberManager = new LinkGrabberManager();
        this.linkGrabber = new LinkGrabberMessageListener(this.linkGrabberManager);
        
        this.linkGrabberManager.addLinkGrabber(new YouTubeLinkGrabber());
        
        myPolly.irc().addMessageListener(this.linkGrabber);
    }

    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        
        this.getMyPolly().irc().removeMessageListener(this.linkGrabber);
    }
}
