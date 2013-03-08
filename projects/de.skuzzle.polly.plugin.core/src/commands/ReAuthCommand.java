package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class ReAuthCommand extends Command {

    public ReAuthCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "reauth");
        this.createSignature(
                "Setzt den Standardnickname von polly und identifiziert " +
        		"sich mit Nickserv", 
        		MyPlugin.SET_AND_IDENTIFY_PERMISSION);
        this.createSignature(
            "Setzt den Standardnickname von polly und identifiziert " +
            "sich mit Nickserv. Zusätzlich werden alle Channels gejoined.",
            MyPlugin.SET_AND_IDENTIFY_PERMISSION,
            new Parameter("Join", Types.BOOLEAN));
        
        this.setHelpText("Setzt den Standardnickname von polly und identifiziert " +
            "sich mit Nickserv");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            this.getMyPolly().irc().setAndIdentifyDefaultNickname();
        } else if (this.match(signature, 1)) {
            this.getMyPolly().irc().setAndIdentifyDefaultNickname();
            this.getMyPolly().irc().rejoinDefaultChannels();
        }
        
        return false;
    }

}
