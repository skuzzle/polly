package commands;

import core.HighlightReplyHandler;
import core.HighlightReplyHandler.Mode;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;

public class HighlightModeCommand extends Command {

    private HighlightReplyHandler hlHandler;

    
    public HighlightModeCommand(MyPolly polly, HighlightReplyHandler hlHandler) 
                throws DuplicatedSignatureException {
        super(polly, "hlctrl");
        this.createSignature("Commands: OFF, COLLECTING, REPLYING, BOTH, RESET_TIME", 
                new Parameter("Modus", Types.newString()));
        this.setHelpText("Setzt den Modus für den Highlight-Replyer");
        this.setUserLevel(UserManager.ADMIN);
        this.setRegisteredOnly();
        this.hlHandler = hlHandler;
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
    
        if (this.match(signature, 0)) {
            String newMode = signature.getStringValue(0);

            if (newMode.equalsIgnoreCase("RESET_TIME")) {
                this.hlHandler.resetTimeOut();
                this.reply(channel, "Delay reset");
                return false;
            }
            for (Mode mode : Mode.values()) {
                if (mode.toString().equalsIgnoreCase(newMode)) {
                    this.hlHandler.setMode(mode);
                    this.reply(channel, "Set new mode: " + mode);
                    break;
                }
            }
        }
        
        return false;
    }

}
