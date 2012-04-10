package commands;


import java.util.Date;

import core.RemindManagerImpl;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;


public class ModRemindCommand extends AbstractRemindCommand {

    public ModRemindCommand(MyPolly polly, RemindManagerImpl manager) throws DuplicatedSignatureException {
        super(polly, manager, "modr");
        this.createSignature("Ändert das Datum des Reminds mit der angegebenen ID", 
            new Parameter("Remind-Id", Types.NUMBER), 
            new Parameter("Neue Zeit", Types.DATE));
        this.createSignature("Ändert die Nachricht des angegebenen Reminds", 
            new Parameter("Remind-Id", Types.NUMBER), 
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Ändert Nachricht und Datum des angegebenen Reminds", 
            new Parameter("Remind-Id", Types.NUMBER), 
            new Parameter("Nachricht", Types.STRING), 
            new Parameter("Meue Zeit", Types.DATE));
        this.setHelpText("Mit diesem Befehl können bestehende Reminds modifiziert " +
        		"werden. Zum rausfinden der ID eines Reminds benutze :myreminds");
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        int id = (int) signature.getNumberValue(0);
        RemindEntity remind = this.remindManagerImpl.getRemind(id);

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
            this.remindManagerImpl.modifyRemind(executer, id, dueDate, message);
            this.reply(channel, "Remind erfolgreich aktualisiert");
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
        
        return false;
    }

}
