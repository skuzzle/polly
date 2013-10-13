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
import entities.RemindEntity;


public class ModRemindCommand extends AbstractRemindCommand {

    public ModRemindCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "modr"); //$NON-NLS-1$
        this.createSignature(MSG.modRemindSig0Desc,
            MyPlugin.MODIFY_REMIND_PERMISSION,
            new Parameter(MSG.modRemindSig0Id, Types.NUMBER), 
            new Parameter(MSG.modRemindSig0NewTime, Types.DATE));
        this.createSignature(MSG.modRemindSig1Desc, 
            MyPlugin.MODIFY_REMIND_PERMISSION,
            new Parameter(MSG.modRemindSig1Id, Types.NUMBER), 
            new Parameter(MSG.modRemindSig1Message, Types.STRING));
        this.createSignature(MSG.modRemindSig2Desc,
            MyPlugin.MODIFY_REMIND_PERMISSION,
            new Parameter(MSG.modRemindSig2Id, Types.NUMBER), 
            new Parameter(MSG.modRemindSig2Message, Types.STRING), 
            new Parameter(MSG.modRemindSig2NewTime, Types.DATE));
        this.createSignature(MSG.modRemindSig3Desc, 
            MyPlugin.MODIFY_REMIND_PERMISSION,
            new Parameter(MSG.modRemindSig3NewTime, Types.DATE));
        this.createSignature(MSG.modRemindSig4Desc, 
            MyPlugin.MODIFY_REMIND_PERMISSION,
            new Parameter(MSG.modRemindSig4Message, Types.STRING));
        this.createSignature(MSG.modRemindSig5Desc,
            MyPlugin.MODIFY_REMIND_PERMISSION,
            new Parameter(MSG.modRemindSig5Message, Types.STRING), 
            new Parameter(MSG.modRemindSig5NewTime, Types.DATE));
        this.setHelpText(MSG.modRemindHelp);
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (signature.getId() >= 3) {
            final RemindEntity re = this.remindManager.getLastRemind(executer);
            
            if (re == null) {
                throw new CommandException(MSG.modRemindNoRemind);
            }
            
            Date dueDate = re.getDueDate();
            String msg = re.getMessage();
            
            if (this.match(signature, 3)) {
                dueDate = signature.getDateValue(0);
            } else if (this.match(signature, 4)) {
                msg = signature.getStringValue(0);
            } else if (this.match(signature, 5)) {
                msg = signature.getStringValue(0);
                dueDate = signature.getDateValue(1);
            }
            
            try {
                this.remindManager.modifyRemind(executer, re.getId(), dueDate, msg);
                this.reply(channel, MSG.modRemindSuccess);
                return false;
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }

        
        int id = (int) signature.getNumberValue(0);
        RemindEntity remind = this.remindManager.getDatabaseWrapper().getRemind(id);

        Date dueDate = remind.getDueDate();
        String message = remind.getMessage();
        
        if (this.match(signature, 0)) {
            dueDate = signature.getDateValue(1);
        } else if (this.match(signature, 1)) {
            message = signature.getStringValue(1);
        } else if (this.match(signature, 2)) {
            message = signature.getStringValue(1);
            dueDate = signature.getDateValue(2);
        }
        
        try {
            this.remindManager.modifyRemind(executer, id, dueDate, message);
            this.reply(channel, MSG.modRemindSuccess);
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
        
        return false;
    }

}
