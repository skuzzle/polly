package commands;


import polly.reminds.MSG;
import core.RemindFormatter;
import core.RemindManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.RemindEntity;



public class AbstractRemindCommand extends Command {
    
    protected RemindManager remindManager;

    protected final static RemindFormatter FORMATTER = new RemindFormatter() {
        
        @Override
        protected String formatRemind(RemindEntity remind, FormatManager formatter) {
            // ISSUE: 0000032, fixed
            return MSG.bind(MSG.abstractRemindCommandRemindFormat, 
                    remind.getForUser(), 
                    formatter.formatDate(remind.getDueDate()), 
                    remind.getId());
        }
        
        @Override
        protected String formatMessage(RemindEntity remind, FormatManager formatter) {
            return MSG.bind(MSG.abstractRemindCommandMessageFormat, 
                    remind.getForUser(), 
                    formatter.formatDate(remind.getDueDate()), 
                    remind.getId());
        }
    };
    
    
    
    public AbstractRemindCommand(MyPolly polly, RemindManager remindManager, 
            String commandName) {
        super(polly, commandName);
        this.remindManager = remindManager;
    }
    
    
    
    protected void addRemind(User executer, RemindEntity remind, boolean schedule) 
            throws CommandException {
        try {
            this.remindManager.addRemind(executer, remind, schedule);
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
    }
    
}