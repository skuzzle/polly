package commands;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import polly.reminds.MyPlugin;

import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.time.Time;
import entities.RemindEntity;



public class RemindCommand extends AbstractRemindCommand {
	
	protected Logger logger = Logger.getLogger(RemindCommand.class.getName());

	public RemindCommand(MyPolly myPolly, RemindManager remindManager) 
            throws DuplicatedSignatureException {
        super(myPolly, remindManager, "remind");
        this.createSignature("Erinnert den Benutzer zu einer angegebenen Zeit im " +
                "angegebenen Channel an etwas.", 
            MyPlugin.REMIND_PERMISSION,
            new Parameter("User", Types.USER), 
            new Parameter("Channel", Types.CHANNEL), 
            new Parameter("Zeit", Types.DATE), 
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Erinnert eine Liste von Benutzern zu einer angegebenen " +
                "Zeit an etwas.",
            MyPlugin.REMIND_PERMISSION,
            new Parameter("Benutzerliste", new ListType(Types.USER)), 
            new Parameter("Channel", Types.CHANNEL), 
            new Parameter("Zeit", Types.DATE), 
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Erinnert dich zu einer bestimmten Zeit an etwas.",
            MyPlugin.REMIND_PERMISSION,
            new Parameter("Zeit", Types.DATE), 
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Erinnert den angegebenen Benutzer per Query an etwas.",
            MyPlugin.REMIND_PERMISSION,
            new Parameter("User", Types.USER), 
            new Parameter("Zeit", Types.DATE), 
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Erinnert dich zu einer bestimmten Zeit mit einer " +
        		"Standardmeldung.",
    		MyPlugin.REMIND_PERMISSION,
    		new Parameter("Zeit", Types.DATE));
        this.createSignature(
            "Erinnert dich nach einer bestimmten Zeit mit einer Standardmeldung.");
        this.setRegisteredOnly();
        this.setHelpText("Hinterlässt Erinnerungen für Benutzer.");
    }
	
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
               
        if (this.match(signature, 0)) {
            String location = signature.getStringValue(1);
            Date dueDate = signature.getDateValue(2);
            String msg = signature.getStringValue(3);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, location, 
                    dueDate);
            this.addRemind(remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
            
        } else if (this.match(signature, 1)) {
            String location = signature.getStringValue(1);
            Date dueDate = signature.getDateValue(2);
            String msg = signature.getStringValue(3);
            String fromUser = executer.getCurrentNickName();
            
            List<UserType> users = signature.getListValue(UserType.class, 0);
            for (UserType ut : users) {
                RemindEntity remind = new RemindEntity(msg, fromUser, ut.getValue(), 
                        location, dueDate);
                this.addRemind(remind, true);
            }
            ListType tmp = (ListType) signature.getValue(0);
            this.reply(channel, "Erinnerungen für die Benutzer " + 
                    tmp.valueString(this.getMyPolly().formatting()) + " hinzugefügt.");
            
        } else if (this.match(signature, 2)) {
            Date dueDate = signature.getDateValue(0);
            String msg = signature.getStringValue(1);
            
            RemindEntity remind = new RemindEntity(msg, executer.getCurrentNickName(), 
                    executer.getCurrentNickName(), channel, dueDate);
            this.addRemind(remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        } else if (this.match(signature, 3)) {
            /*
             * ISSUE: 0000021
             * This signatures allows to create reminds that are delivered via query
             */
            String forUser = signature.getStringValue(0);
            Date dueDate = signature.getDateValue(1);
            String msg = signature.getStringValue(2);
            String fromUser = executer.getCurrentNickName();
            

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, forUser, 
                    dueDate);
            this.addRemind(remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        } else  if (this.match(signature, 4)){
        	Date dueDate = signature.getDateValue(0);
        	String msg = "Reminder";
        	try {
                msg = executer.getAttribute(MyPlugin.DEFAULT_MSG);
            } catch (UnknownAttributeException e) {
                throw new CommandException(e);
            }
            
            RemindEntity remind = new RemindEntity(msg, executer.getCurrentNickName(), 
                    executer.getCurrentNickName(), channel, dueDate);
            this.addRemind(remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        } else if (this.match(signature, 5)) {
            String msg = "Reminder";
            long millis = Time.currentTimeMillis();
            try {
                msg = executer.getAttribute(MyPlugin.DEFAULT_MSG);
                millis += Long.parseLong(
                    executer.getAttribute(MyPlugin.DEFAULT_REMIND_TIME));
            } catch (UnknownAttributeException e) {
                throw new CommandException(e);
            }
            Date dueDate = new Date(millis);
            RemindEntity remind = new RemindEntity(msg, executer.getCurrentNickName(), 
                    executer.getCurrentNickName(), channel, dueDate);
            this.addRemind(remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        }
        return false;
    }
}
