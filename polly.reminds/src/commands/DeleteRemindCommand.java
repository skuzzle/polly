package commands;

import core.RemindManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;


public class DeleteRemindCommand extends Command {

    private RemindManager remindManager;
    
    public DeleteRemindCommand(MyPolly polly, RemindManager manager) throws DuplicatedSignatureException {
        super(polly, "delremind");
        this.createSignature("Löscht die Erinnerung mit der angegebenen Id", new NumberType());
        this.remindManager = manager;
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            int remindId = (int) signature.getNumberValue(0);
            
            RemindEntity remind = this.remindManager.getRemind(remindId);
            if (remind == null) {
                this.reply(channel, "Keine Erinnerung mit der angegebenen ID vorhanden");
                return false;
            }
            
            if (!remind.getForUser().equals(executer.getCurrentNickName())) {
                this.reply(channel, 
                        "Du kannst nur für dich bestimmte Erinnerungen löschen.");
                return false;
            }
            
            this.remindManager.unSchedule(remindId);
            this.remindManager.deleteRemind(remindId);
            this.reply(channel, "Erinnerung wurde gelöscht");
        }
        
        return false;
    }
}
