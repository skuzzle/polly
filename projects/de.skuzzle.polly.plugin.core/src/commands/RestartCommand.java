package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class RestartCommand extends Command {

    public RestartCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "restart");
        this.createSignature("Startet polly mit den aktuellen Parametern neu.", 
            MyPlugin.RESTART_PERMISSION);
        this.createSignature("Startet polly mit den angegebenen Parametern neu.",
            MyPlugin.RESTART_PERMISSION,
            new Parameter("Parameter", Types.STRING));
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
