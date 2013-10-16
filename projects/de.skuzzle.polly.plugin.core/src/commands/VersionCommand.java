package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class VersionCommand extends Command {

    public VersionCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "version"); //$NON-NLS-1$
        this.createSignature(MSG.versionSig0Desc.s);
        this.setHelpText(MSG.versionHelp.s);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        this.reply(channel, MSG.versionPollyVersion.s( 
                this.getMyPolly().getPollyVersion()));
        return false;
    }
}
