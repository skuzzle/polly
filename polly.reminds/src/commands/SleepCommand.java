package commands;

import java.util.Date;

import core.RemindManagerImpl;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;

public class SleepCommand extends Command {

    private RemindManagerImpl remindManagerImpl;
    
    public SleepCommand(MyPolly polly, RemindManagerImpl remindManagerImpl) 
            throws DuplicatedSignatureException {
   
        super(polly, "sleep");
        this.remindManagerImpl = remindManagerImpl;
        this.createSignature("Verlängert die Erinnerung die dir zuletzt zugestellt wurde", 
            new Parameter("Neue Zeit", Types.DATE));
        this.setRegisteredOnly();
        this.setHelpText("Verlängert Erinnerungen");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        if (this.match(signature, 0)) {
            Date dueDate = signature.getDateValue(0);
            RemindEntity sleeping = this.remindManagerImpl.getSleep(
                    executer.getCurrentNickName());
            
            if (sleeping == null) {
                this.reply(channel, "Kein Remind aktiv.");
                return false;
            }
            
            RemindEntity copy = sleeping.copyForNewDueDate(dueDate);
            try {
                this.remindManagerImpl.addRemind(copy);
                this.remindManagerImpl.scheduleRemind(copy, dueDate);
                this.remindManagerImpl.removeSleep(sleeping.getForUser());
                this.reply(channel, "Erinnerung wurde verlängert.");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }

}
