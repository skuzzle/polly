package commands;

import java.util.Date;

import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;


public class MailRemindCommand extends AbstractRemindCommand {

    public MailRemindCommand(MyPolly polly, RemindManager manager) 
                throws DuplicatedSignatureException {
        super(polly, manager, "mremind");
        this.createSignature("Sendet eine Erinnerung zur angegebenen Zeit an den " +
        		"angegebenen User", 
    		new Parameter("User", Types.USER), 
    		new Parameter("Datum", Types.DATE), 
    		new Parameter("Nachricht", Types.STRING));
        
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
        throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            User user = this.getMyPolly().users().getUser(signature.getStringValue(0));
            if (user == null) {
                this.reply(channel, "Unbekannter Benutzer: " + 
                    signature.getStringValue(0));
                return false;
            }
            Date dueDate = signature.getDateValue(1);
            String message = signature.getStringValue(2);
            
            RemindEntity re = new RemindEntity(message, executer.getName(), 
                user.getName(), channel, dueDate, false, true);
            try {
                this.remindManager.addRemind(re);
                this.remindManager.scheduleRemind(re, dueDate);
                this.reply(channel, "E-Mail Nachricht für " + user.getName() + 
                    " hinterlassen");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }

}
