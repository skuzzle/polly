package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class ReAuthCommand extends Command {

    public ReAuthCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "reauth");
        this.createSignature(
                "Setzt den Standardnickname von polly und identifiziert " +
        		"sich mit Nickserv", 
        		MyPlugin.SET_AND_IDENTIFY_PERMISSION);
        this.setHelpText("Setzt den Standardnickname von polly und identifiziert " +
            "sich mit Nickserv");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            this.getMyPolly().irc().setAndIdentifyDefaultNickname();
        }
        
        return false;
    }

}
