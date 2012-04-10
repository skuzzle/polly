package commands;


import java.util.Date;
import java.util.List;

import core.RemindManager;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;



public class LeaveCommand extends AbstractRemindCommand {


    public LeaveCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "leave");
        this.createSignature("Hinterlässt eine Nachricht für einen Benutzer.", 
                new Parameter("User", Types.USER), 
                new Parameter("Channel", Types.CHANNEL), 
                new Parameter("Nachricht", Types.STRING));
        this.createSignature("Hinterlässt eine Nachricht für eine Liste von Benutzern.", 
                new Parameter("Benutzerliste", new ListType(Types.USER)), 
                new Parameter("Channel", Types.CHANNEL), 
                new Parameter("Nachricht", Types.STRING));
        this.createSignature("Hinterlässt eine private Nachricht für einen Benutzer.", 
                new Parameter("User", Types.USER), 
                new Parameter("Nachricht", Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText("Befehl um Nachrichten für Benutzer zu hinterlassen.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
             
        if (this.match(signature, 0)) {
            String location = signature.getStringValue(1);
            String msg = signature.getStringValue(2);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, location, 
                    new Date());
            remind.setIsMessage(true);
            this.addRemind(remind, false);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
            
        } else if (this.match(signature, 1)) {
            String location = signature.getStringValue(1);
            String msg = signature.getStringValue(2);
            String fromUser = executer.getCurrentNickName();
            
            List<UserType> users = signature.getListValue(UserType.class, 0);
            for (UserType ut : users) {
                RemindEntity remind = new RemindEntity(msg, fromUser, ut.getValue(), 
                        location, new Date());
                remind.setIsMessage(true);
                this.addRemind(remind, false);
            }
            ListType tmp = (ListType) signature.getValue(0);
            this.reply(channel, "Erinnerungen für die Benutzer " + 
                    tmp.valueString(this.getMyPolly().formatting()) + " hinzugefügt.");
            
        } else if (this.match(signature, 2)) {
            String msg = signature.getStringValue(1);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);
            
            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, forUser, 
                    new Date());
            remind.setIsMessage(true);
            this.addRemind(remind, false);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
            
        }
        return false; 
    }

}