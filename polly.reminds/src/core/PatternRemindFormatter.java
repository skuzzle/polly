package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.RemindEntity;

public class PatternRemindFormatter extends RemindFormatter {
    
    public static RemindFormatter forPattern(String pattern) {
        return new PatternRemindFormatter(pattern, false);
    }
    
    
    
    public static RemindFormatter forPattern(String pattern, boolean escape) {
        return new PatternRemindFormatter(pattern, escape);
    }
    
    
    
    private String pattern;
    private boolean escape;
    
    
    private PatternRemindFormatter(String pattern, boolean escape) {
        this.pattern = pattern;
        this.escape = escape;
    }
    
    /*
     * %m = message
     * %s = sender
     * %r = receiver
     * %dd = duedate
     * %ld = leavedate
     */
    
    private String escape(String other) {
        if (!this.escape) {
            return other;
        }
        return other.replaceAll("%s%|%m%|%r%|%dd%|%ld%", "");
    }



    
    @Override
    protected String formatMessage(RemindEntity remind, FormatManager formatter) {
        return this.parse(remind, formatter);
    }



    @Override
    protected String formatRemind(RemindEntity remind, FormatManager formatter) {
        return this.parse(remind, formatter);
    }
    
    
    
    private String parse(RemindEntity remind, FormatManager formatter) {
        String tmp = this.pattern.replaceAll("%m%", this.escape(remind.getMessage()));
        tmp = tmp.replaceAll("%s%", this.escape(remind.getFromUser()));
        tmp = tmp.replaceAll("%r%", this.escape(remind.getForUser()));
        tmp = tmp.replaceAll("%dd%", formatter.formatDate(remind.getDueDate()));
        tmp = tmp.replaceAll("%ld%", formatter.formatDate(remind.getLeaveDate()));
        return tmp;
    }
}