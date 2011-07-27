package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


/**
 * 
 * @author Simon
 * @version 27.07.2011 3851c1b
 */
public class QuitCommand extends Command {

    public QuitCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "flyaway");
        this.createSignature("Beendet polly.");
        this.createSignature("Beendet polly mit der angegebenen Quit-Message", 
                new StringType());
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Befehl zum Beenden von Polly.");
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {

        String message = "*krächz* *krächz* *krächz*";
        if (this.match(signature, 1)) {
            message = signature.getStringValue(0);
        }
        
        this.getMyPolly().irc().quit(message);
        this.getMyPolly().shutdownManager().shutdown();
        return false;
    }
}
