package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.RemindEntity;

public abstract class RemindFormatter {
    
    public String format(RemindEntity remind, FormatManager formatter) {
        return remind.isMessage() 
            ? this.formatMessage(remind, formatter) 
            : this.formatRemind(remind, formatter);
    }
    
    protected abstract String formatRemind(RemindEntity remind, FormatManager formatter);
    
    protected abstract String formatMessage(RemindEntity remind, FormatManager formatter);
}