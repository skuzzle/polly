package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;


public class SignOffCommand extends Command {

    public SignOffCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "signoff"); //$NON-NLS-1$
        this.createSignature(MSG.signOffSig0Desc);
        this.setRegisteredOnly();
        this.setHelpText(MSG.signOffHelp);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        try {
            this.getMyPolly().users().logoff(executer);
            this.reply(channel, MSG.signOffSuccess);
        } catch (UnknownUserException e) {
            e.printStackTrace();
        }
        return false;
    }
}
