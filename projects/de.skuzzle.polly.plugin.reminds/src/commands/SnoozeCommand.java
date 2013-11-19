package commands;

import java.util.Date;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;
import entities.RemindEntity;

public class SnoozeCommand extends AbstractRemindCommand {

    
    public SnoozeCommand(MyPolly polly, RemindManager remindManager) 
            throws DuplicatedSignatureException {
        super(polly, remindManager, "snooze"); //$NON-NLS-1$
        this.createSignature(MSG.snoozeSig0Desc,
            MyPlugin.SNOOZE_PERMISSION,
            new Parameter(MSG.snoozeSig0NewTime, Types.DATE));
        this.createSignature(MSG.snoozeSig1Desc);
        this.setRegisteredOnly();
        this.setHelpText(MSG.snoozeHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            Date dueDate = signature.getDateValue(0);
            
            try {
                final RemindEntity re = this.remindManager.snooze(executer, dueDate);
                this.reply(channel, MSG.bind(MSG.snoozeSuccess, 
                        this.getMyPolly().formatting().formatDate(dueDate), re.getId()));
                
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        } else if (this.match(signature, 1)) {
            try {
                final RemindEntity re = this.remindManager.snooze(executer);
                this.reply(channel, MSG.bind(MSG.snoozeSuccess, 
                        this.getMyPolly().formatting().formatDate(re.getDueDate()), 
                        re.getId()));
                
            } catch (UnknownAttributeException e) {
                throw new CommandException(e);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
