package polly.linkexpander.core.grabbers;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageType;



public class URLLinkGrabber implements LinkGrabber {

    private final static Pattern URL_PATTERN = Pattern.compile(
        "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"); //$NON-NLS-1$
    
    private List<MessageEvent> urls;
    
    
    
    public URLLinkGrabber() {
        this.urls = new LinkedList<MessageEvent>();
    }
    
    
    
    public List<MessageEvent> getUrls() {
        return this.urls;
    }
    
    
    
    @Override
    public Pattern getLinkPattern() {
        return URL_PATTERN;
    }

    

    @Override
    public String processMatch(MessageEvent matched, Matcher matcher) {
        MessageEvent e = new MessageEvent(
            matched.getSource(), matched.getUser(), MessageType.PUBLIC, matched.getChannel(), 
            this.getLink(matched.getMessage(), matcher));
        this.urls.add(e);
        
        return null;
    }
    
    

    @Override
    public String getLink(String input, Matcher matcher) {
        return new String(matcher.group());
    }
}
