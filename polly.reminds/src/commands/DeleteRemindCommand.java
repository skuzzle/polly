package commands;

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


public class DeleteRemindCommand extends Command {

    private RemindManagerImpl remindManagerImpl;
    
    public DeleteRemindCommand(MyPolly polly, RemindManagerImpl manager) throws DuplicatedSignatureException {
        super(polly, "delremind");
        this.createSignature("Löscht die Erinnerung mit der angegebenen Id", 
            new Parameter("Remind-ID", Types.NUMBER));
        this.setRegisteredOnly();
        this.remindManagerImpl = manager;
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            int remindId = (int) signature.getNumberValue(0);
            
            try {
                this.remindManagerImpl.deleteRemind(remindId, executer);
                this.reply(channel, "Erinnerung wurde gelöscht");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }
}
