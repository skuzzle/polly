package core;

import polly.reminds.MSG;
import de.skuzzle.polly.sdk.FormatManager;
import entities.RemindEntity;


public class HeidiRemindFormatter extends RemindFormatter {
    
    private final String[] names = MSG.heidiNickNames.split(","); //$NON-NLS-1$
    
    
    // "@%r%: %m%. (Hinterlassen von: %s% am %ld%)
    @Override
    protected String formatRemind(RemindEntity remind, FormatManager formatter) {
        return MSG.bind(MSG.myRemindFormatRemind, remind.getId(), 
                this.chooseName(), 
                remind.getMessage(), 
                formatter.formatDate(remind.getDueDate()), 
                formatter.formatDate(remind.getLeaveDate()), 
                remind.getFromUser());
    }

    
    
    @Override
    protected String formatMessage(RemindEntity remind, FormatManager formatter) {
        return MSG.bind(MSG.myRemindFormatMessage, remind.getId(), 
                this.chooseName(), 
                remind.getMessage(), 
                formatter.formatDate(remind.getDueDate()), 
                formatter.formatDate(remind.getLeaveDate()), 
                remind.getFromUser());
    }

    
    
    private String chooseName() {
        return this.names[(int)(Math.random() * this.names.length)];
    }
}
