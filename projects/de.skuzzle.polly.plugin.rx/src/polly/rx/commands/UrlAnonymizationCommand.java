package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.captcha.Anonymizer;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class UrlAnonymizationCommand extends Command {

    public UrlAnonymizationCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "anonymize"); //$NON-NLS-1$
        
        this.createSignature(MSG.anonymizationSig0Desc,
                RoleManager.ADMIN_PERMISSION,
                new Parameter(MSG.anonymizationSig0Name, Types.BOOLEAN));
        this.setHelpText(MSG.anonymizationHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final boolean activation = signature.getBooleanValue(0);
            Anonymizer.setAnonymize(activation);
            if (activation) {
                this.reply(channel, MSG.anonymizationOn);
            } else {
                this.reply(channel, MSG.anonymizationOff);
            }
        }
        
        return false;
    }
}
