package polly.linkexpander.core;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;

import polly.linkexpander.core.grabbers.LinkGrabber;


public class LinkGrabberManager {

    private List<LinkGrabber> grabbers;
    
    
    public LinkGrabberManager() {
        this.grabbers = new LinkedList<LinkGrabber>();
    }
    
    
    
    public void addLinkGrabber(LinkGrabber grabber) {
        this.grabbers.add(grabber);
    }
    
    
    
    public void processMessageEvent(MessageEvent e) {
        for (LinkGrabber grabber : this.grabbers) {
            Matcher m = grabber.getLinkPattern().matcher(e.getMessage());
            if (m.find()) {
                String r = grabber.processMatch(e.getMessage(), m);
                if (r != null) {
                    e.getSource().sendMessage(e.getChannel(),  
                        "Video Titel (gepostet von " + e.getUser() + "): " + r, this);
                }
            }
        }
    }
}