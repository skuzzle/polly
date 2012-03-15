package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class RawIrcCommand extends Command {

    public RawIrcCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "raw");
        this.createSignature("Sendet einen Raw-command an den IRC Server.", 
                new Parameter("Befehl", new StringType()));
        this.setRegisteredOnly();
        this.setHelpText("Dieser Befehl sendet einen Befehl direkt an den IRC Server");
        this.setUserLevel(UserManager.ADMIN);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            this.getMyPolly().irc().sendRawCommand(signature.getStringValue(0));
        }
        return false;
    }
}
