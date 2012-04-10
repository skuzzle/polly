package commands;

import java.util.Date;

import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;


public class OnReturnCommand extends AbstractRemindCommand {

    public OnReturnCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "onreturn");
        this.createSignature("Hinterlässt eine Nachricht für einen Benutzer die " +
        		"zugestellt wird wenn dieser wieder im IRC aktiv ist", 
    		new Parameter("User", Types.USER), 
    		new Parameter("Nachricht", Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText("Dieser Befehl hinterlässt Nachrichten die zugestellt werden, " +
        		"sobald der Benutzer wieder im IRC aktiv ist.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String msg = signature.getStringValue(1);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, channel, 
                    new Date());
            remind.setIsMessage(true);
            remind.setOnAction(true);
            this.addRemind(remind, false);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        }
        
        return false;
    }

}
