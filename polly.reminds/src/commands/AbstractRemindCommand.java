package commands;


import core.RemindFormatter;
import core.RemindManager;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import entities.RemindEntity;



public class AbstractRemindCommand extends Command {
    
    protected RemindManager remindManager;

    protected final static RemindFormatter FORMATTER = new RemindFormatter() {
        
        @Override
        protected String formatRemind(RemindEntity remind, FormatManager formatter) {
            // ISSUE: 0000032, fixed
            return "Erinnerung für " + remind.getForUser() + " gespeichert. (Fällig: " + 
                formatter.formatDate(remind.getDueDate()) + ", Id: " + remind.getId() + ")";
        }
        
        @Override
        protected String formatMessage(RemindEntity remind, FormatManager formatter) {
            return "Nachricht für " + remind.getForUser() + " hinterlassen. (Id: " + 
                remind.getId() + ")";
        }
    };
    
    
    
    public AbstractRemindCommand(MyPolly polly, RemindManager manager, 
            String commandName) {
        super(polly, commandName);
        this.remindManager = manager;
    }
    
    
    
    protected RemindEntity addRemind(RemindEntity remind, boolean schedule) {
        this.remindManager.addRemind(remind);
        if (schedule) {
            this.remindManager.scheduleRemind(remind, remind.getDueDate());
        }
        return remind;
    }
}