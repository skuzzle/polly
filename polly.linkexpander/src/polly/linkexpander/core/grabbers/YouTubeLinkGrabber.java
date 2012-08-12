package polly.linkexpander.core.grabbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;



public class YouTubeLinkGrabber extends AbstractHttpRequestGrabber {
    
    
    public static void main(String[] args) {
        MessageEvent e = new MessageEvent(null, null, null, 
            "http://www.youtube.com/watch?v=xyy-YY5tt0I&feature=g-user-u");
        
        YouTubeLinkGrabber ytlg = new YouTubeLinkGrabber();
        
        Matcher m = ytlg.getLinkPattern().matcher(e.getMessage());
        if (m.matches()) {
            System.out.println(ytlg.processMatch(e.getMessage(), m));
        }
    }

    private final static Pattern LINK_PATTERN = Pattern.compile(
            "(http://www\\.youtube\\.com/watch\\?v=[a-zA-Z0-9-]+).*");
    
    private final static Pattern META_PATTERN = Pattern.compile(
        "<meta\\s+name=\"title\"\\s+content=\"([^\"]+)\">");
    
    private static final int REQUEST_URL_GROUP = 1;
    private static final int TITLE_GROUP = 1;
    
    
    
    @Override
    public Pattern getLinkPattern() {
        return LINK_PATTERN;
    }

    
    
    @Override
    public String processResponseLine(String line) {
        Matcher m = META_PATTERN.matcher(line);
        if (m.find()) {
            return line.substring(m.start(TITLE_GROUP), m.end(TITLE_GROUP));
        }
        return null;
    }

    
    
    @Override
    public String getLink(String input, Matcher matcher) {
        String url = input.substring(matcher.start(REQUEST_URL_GROUP), 
            matcher.end(REQUEST_URL_GROUP));
        
        return url;
    }

}
