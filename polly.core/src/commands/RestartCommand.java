package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class RestartCommand extends Command {

    public RestartCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "restart");
        this.createSignature("Startet polly mit den aktuellen Parametern neu.");
        this.createSignature("Startet polly mit den angegebenen Parametern neu.", 
            new Parameter("Parameter", Types.newString()));
        this.setHelpText("Startet polly neu.");
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            this.getMyPolly().shutdownManager().restart();
        } else if (this.match(signature, 1)) {
            this.getMyPolly().shutdownManager().restart(signature.getStringValue(0));
        }
        
        
        return false;
    }

}
