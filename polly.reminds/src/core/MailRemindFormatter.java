package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.RemindEntity;


public class MailRemindFormatter extends RemindFormatter {

    private final static String MESSAGE = 
        "Hi %s, \n\n" + 
        "%s hat dir im Channel %s um %s folgende Nachricht hinterlassen:\n" +
        "Erinnerung um %s: %s\n\n Bye\npolly";
    
    
    @Override
    protected String formatRemind(RemindEntity remind, FormatManager formatter) {
        return String.format(MESSAGE, 
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
