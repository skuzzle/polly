package polly.rx.parsing;

import java.util.regex.Matcher;


public final class RegexUtils {
    
    private RegexUtils() {}
    
    
    public final static int subint(String orig, Matcher m, int groupId) {
        if (m.start(groupId) == -1) {
            return 0;
        }
        return Integer.parseInt(substr(orig, m, groupId));
    }
    
    
    
    public final static String substr(String orig, int beginIndex, int endIndex) {
        return new String(orig.substring(beginIndex, endIndex));
    }
    
    
    
    public final static String substr(String orig, Matcher m, int groupId) {
        if (m.start(groupId) == -1) {
            return "";
        }
        return substr(orig, m.start(groupId), m.end(groupId));
    }
    
}
