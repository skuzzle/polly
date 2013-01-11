package polly.linkexpander.core.grabbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NineGagLinkGrabber extends AbstractHttpRequestGrabber {
    
    private final static Pattern LINK_PATTERN = Pattern.compile(
        "http://9gag.com/gag/\\d+");
    
    private final static Pattern TITLE_PATTERN = Pattern.compile(
        "<title>([^<]+)</title>");
    
    
    
    @Override
    public Pattern getLinkPattern() {
        return LINK_PATTERN;
    }

    
    
    @Override
    public String getLink(String input, Matcher matcher) {
        return new String(matcher.group());
    }
    
    

    @Override
    public String processResponseLine(String line) {
        final Matcher m = TITLE_PATTERN.matcher(line);
        if (m.find()) {
            return new String(line.substring(m.start(1), m.end(1)));
        }
        return null;
    }

}
