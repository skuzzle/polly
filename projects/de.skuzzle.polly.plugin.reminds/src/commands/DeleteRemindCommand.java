package commands;

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


public class DeleteRemindCommand extends AbstractRemindCommand {

    
    public DeleteRemindCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "delremind"); //$NON-NLS-1$
        this.createSignature(MSG.delRemindSig0Desc, 
            MyPlugin.DELETE_REMIND_PERMISSION,
            new Parameter(MSG.delRemindSig0Id, Types.NUMBER));
        this.createSignature(MSG.delRemindSig1Desc);
        this.setRegisteredOnly();
        this.setHelpText(MSG.delRemindHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            int remindId = (int) signature.getNumberValue(0);
            
            try {
                this.remindManager.deleteRemind(executer, remindId);
                this.reply(channel, MSG.delRemindSuccess);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        } else if (this.match(signature, 1)) {
            try {
                this.remindManager.deleteRemind(executer);
                this.reply(channel, MSG.delRemindSuccess);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }
}
