package polly.linkexpander.core;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;


public class LinkGrabberMessageListener implements MessageListener {

    private LinkGrabberManager linkGrabber;
    
    
    public LinkGrabberMessageListener(LinkGrabberManager linkGrabber) {
        this.linkGrabber = linkGrabber;
    }
    
    
    
    @Override
    public void publicMessage(MessageEvent e) {
        this.linkGrabber.processMessageEvent(e);
    }
    
    

    @Override
    public void privateMessage(MessageEvent e) {
        this.linkGrabber.processMessageEvent(e);
    }
    
    

    @Override
    public void actionMessage(MessageEvent e) {}

    @Override
    public void noticeMessage(MessageEvent e) {}

}
