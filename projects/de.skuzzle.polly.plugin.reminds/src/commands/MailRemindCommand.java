package commands;

import java.util.Date;

import polly.reminds.MyPlugin;

import core.RemindManager;
import de.skuzzle.polly.sdk.DelayedCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
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
        super(polly, "mremind", 30000);
        this.remindManager = manager;
        this.createSignature("Sendet eine Erinnerung zur angegebenen Zeit an den " +
        		"angegebenen User", 
    		MyPlugin.MAIL_REMIND_PERMISSION,
    		new Parameter("User", Types.USER), 
    		new Parameter("Datum", Types.DATE), 
    		new Parameter("Nachricht", Types.STRING));
        this.createSignature("Sendet eine Erinngerung zur angegebenen Zeit an dich",
            MyPlugin.MAIL_REMIND_PERMISSION,
            new Parameter("Datum", Types.DATE),
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Sendet eine Erinnerung mit Standardtext zur angegebenen " +
        		"Zeit an dich.", 
    		MyPlugin.MAIL_REMIND_PERMISSION,
    		new Parameter("Datum", Types.DATE));
        
        this.setHelpText("Speichert Erinnerungen die per Mail zugestellt werden.");
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
        throws CommandException, InsufficientRightsException {
        
        User user = null;
        Date dueDate = null;
        String message = "";
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
            message = user.getAttribute(MyPlugin.DEFAULT_MSG);
        }
        
        if (user == null) {
            this.reply(channel, "Unbekannter Benutzer: " + 
                signature.getStringValue(0));
            return false;
        }

        
        RemindEntity re = new RemindEntity(message, executer.getName(), 
            user.getName(), channel, dueDate, false, true, Time.currentTime());

        try {
            this.remindManager.addRemind(executer, re, true);
            this.reply(channel, "E-Mail Nachricht für " + user.getName() + 
                " hinterlassen (ID: " + re.getId() + ")");
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
        
        return false;
    }
}
