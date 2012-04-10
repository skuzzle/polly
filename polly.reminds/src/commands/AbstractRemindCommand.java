package commands;


import core.RemindFormatter;
import core.RemindManagerImpl;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.RemindEntity;



public class AbstractRemindCommand extends Command {
    
    protected RemindManagerImpl remindManagerImpl;

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
    
    
    
    public AbstractRemindCommand(MyPolly polly, RemindManagerImpl manager, 
            String commandName) {
        super(polly, commandName);
        this.remindManagerImpl = manager;
    }
    
    
    
    protected RemindEntity addRemind(RemindEntity remind, boolean schedule) 
                throws CommandException {
        try {
            this.remindManagerImpl.addRemind(remind);
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
        if (schedule) {
            this.remindManagerImpl.scheduleRemind(remind, remind.getDueDate());
        }
        return remind;
    }
}