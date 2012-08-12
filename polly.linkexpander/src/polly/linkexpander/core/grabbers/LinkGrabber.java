package polly.linkexpander.core.grabbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public interface LinkGrabber {

    public abstract Pattern getLinkPattern();
    
    public abstract String processMatch(String grabbed, Matcher matcher);
}