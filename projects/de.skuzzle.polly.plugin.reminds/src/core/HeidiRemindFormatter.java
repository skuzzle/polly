package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.RemindEntity;


public class HeidiRemindFormatter extends RemindFormatter {
    
    private String[] names = {"Heidi", "Clum", "Yvonne", "Ivy", "Clumi", "Zicke"};
    
    
    // "@%r%: %m%. (Hinterlassen von: %s% am %ld%)
    @Override
    protected String formatRemind(RemindEntity remind, FormatManager formatter) {
        return this.chooseName() + ": " + remind.getMessage() + ". (Hinterlassen von: " 
            + remind.getFromUser() + " am " + 
            formatter.formatDate(remind.getLeaveDate()) + ")";
    }

    
    
    @Override
    protected String formatMessage(RemindEntity remind, FormatManager formatter) {
        return this.chooseName() + ": " + remind.getMessage() + ". (Hinterlassen von: " 
            + remind.getFromUser() + " am " + 
            formatter.formatDate(remind.getLeaveDate()) + ")";
    }

    
    
    private String chooseName() {
        return this.names[(int)(Math.random() * this.names.length)];
    }
}
