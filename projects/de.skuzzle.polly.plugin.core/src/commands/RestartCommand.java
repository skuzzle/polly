package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class RestartCommand extends Command {

    public RestartCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "restart"); //$NON-NLS-1$
        this.createSignature(MSG.restartSig0Desc, 
            MyPlugin.RESTART_PERMISSION);
        this.createSignature(MSG.restartSig1Desc,
            MyPlugin.RESTART_PERMISSION,
            new Parameter(MSG.restartSig1Params, Types.STRING));
        this.setHelpText(MSG.restartHelp);
        this.setRegisteredOnly();
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
