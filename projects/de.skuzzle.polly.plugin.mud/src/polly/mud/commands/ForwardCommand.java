package polly.mud.commands;

import polly.mud.MudController;
import polly.mud.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class ForwardCommand extends Command {

    public ForwardCommand(MyPolly polly) 
                throws DuplicatedSignatureException {
        super(polly, "forwardMud");
        this.createSignature("Activiert MUD IRC forward", 
            MyPlugin.MUD_PERMISSION,
            new Parameter("Channel", Types.CHANNEL));
        this.createSignature("Deaktiviert MUD IRC forwarding", 
            MyPlugin.MUD_PERMISSION);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            MudController.getInstance().activateForward(signature.getStringValue(0));
        } else if (this.match(signature, 0)) {
            MudController.getInstance().activateForward(null);
        }
        return false;
    }
}
