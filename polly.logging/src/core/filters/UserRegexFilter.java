package core.filters;

import java.util.regex.Pattern;

import entities.LogEntry;


public class UserRegexFilter implements LogFilter {

    private final Pattern pattern;
    
    
    public UserRegexFilter(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
    
    
    @Override
    public boolean accept(LogEntry log) {
        return this.pattern.matcher(log.getNickname()).find();
    }

}
