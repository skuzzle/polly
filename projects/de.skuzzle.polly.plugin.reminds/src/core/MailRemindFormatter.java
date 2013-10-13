package core;

import polly.reminds.MSG;
import de.skuzzle.polly.sdk.FormatManager;
import entities.RemindEntity;


public class MailRemindFormatter extends RemindFormatter {

    private final static String MESSAGE = MSG.mailRemindFormatterMessage;
    
    
    @Override
    protected String formatRemind(RemindEntity remind, FormatManager formatter) {
        return MSG.bind(MESSAGE, 
            remind.getForUser(),
            remind.getFromUser(),
            remind.getOnChannel(),
            formatter.formatDate(remind.getLeaveDate()),
            formatter.formatDate(remind.getDueDate()),
            remind.getMessage());
    }
    
    

    @Override
    protected String formatMessage(RemindEntity remind, FormatManager formatter) {
        return this.formatRemind(remind, formatter);
    }
}
