package polly.linkexpander.core.grabbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polly.linkexpander.MSG;


public class PhpBBLinkGrabber extends AbstractHttpRequestGrabber  {
    
    private final static Pattern LINK_PATTERN = Pattern.compile(
        "\\S+/forum/viewtopic\\.php\\?\\S+"); //$NON-NLS-1$
    
    private final static Pattern TITLE_PATTERN = Pattern.compile(
        "<h2><a[^>]+>([^<]+)"); //$NON-NLS-1$
    
    private final static int  TITLE_GROUP = 1;
    
    
    
    @Override
    public Pattern getLinkPattern() {
        return LINK_PATTERN;
    }
    
    

    @Override
    public String processResponseLine(String line) {
        final Matcher m = TITLE_PATTERN.matcher(line);
        if (m.find()) {
            return MSG.bind(MSG.phpBBTopic,
                line.substring(m.start(TITLE_GROUP), m.end(TITLE_GROUP)));
        }
        return null;
    }

    
    
    @Override
    public String getLink(String input, Matcher matcher) {
        return new String(matcher.group());
    }
}
