package core.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.LogEntry;


public class MessageRegexLogFilter implements LogFilter {

    private Pattern pattern;
    
    
    public MessageRegexLogFilter(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }
    
    
    
    @Override
    public boolean accept(LogEntry log) {
        Matcher m = this.pattern.matcher(log.getMessage());
        return m.matches();
    }
}
