package commands;

import java.util.Date;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.sdk.DelayedCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.time.Time;
import entities.RemindEntity;


public class MailRemindCommand extends DelayedCommand {

    private RemindManager remindManager;
    
    public MailRemindCommand(MyPolly polly, RemindManager manager) 
                throws DuplicatedSignatureException {
        super(polly, "mremind", 30000); //$NON-NLS-1$
        this.remindManager = manager;
        this.createSignature(MSG.mremindSig0Desc, 
    		MyPlugin.MAIL_REMIND_PERMISSION,
    		new Parameter(MSG.mremindSig0User, Types.USER), 
    		new Parameter(MSG.mremindSig0Date, Types.DATE), 
    		new Parameter(MSG.mremindSig0Message, Types.STRING));
        this.createSignature(MSG.mremindSig1Desc,
            MyPlugin.MAIL_REMIND_PERMISSION,
            new Parameter(MSG.mremindSig1Date, Types.DATE),
            new Parameter(MSG.mremindSig1Message, Types.STRING));
        this.createSignature(MSG.mremindSig2Desc, 
    		MyPlugin.MAIL_REMIND_PERMISSION,
    		new Parameter(MSG.mremindSig2Date, Types.DATE));
        
        this.setHelpText(MSG.mremindHelp);
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
        throws CommandException, InsufficientRightsException {
        
        User user = null;
        Date dueDate = null;
        String message = ""; //$NON-NLS-1$
        if (this.match(signature, 0)) {
            user = this.getMyPolly().users().getUser(signature.getStringValue(0));
            dueDate = signature.getDateValue(1);
            message = signature.getStringValue(2);
        } else if (this.match(signature, 1)) {
            user = executer;
            dueDate = signature.getDateValue(0);
            message = signature.getStringValue(1);
        } else if (this.match(signature, 2)) {
            user = executer;
            dueDate = signature.getDateValue(0);
            message = ((StringType) user.getAttribute(MyPlugin.DEFAULT_MSG)).getValue();
        }
        
        if (user == null) {
            this.reply(channel, 
                    MSG.bind(MSG.mremindUnknownUser, signature.getStringValue(0)));
            return false;
        }

        
        RemindEntity re = new RemindEntity(message, executer.getName(), 
            user.getName(), channel, dueDate, false, true, Time.currentTime());

        try {
            this.remindManager.addRemind(executer, re, true);
            this.reply(channel, MSG.bind(MSG.mremindSuccess, user.getName(), re.getId()));
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
        
        return false;
    }
}
