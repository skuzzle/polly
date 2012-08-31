package polly.linkexpander.core.grabbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;


public interface LinkGrabber {

    public abstract Pattern getLinkPattern();
    
    public abstract String processMatch(MessageEvent matched, Matcher matcher);
    
    public abstract String getLink(String input, Matcher matcher);
}