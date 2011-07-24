package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class VersionCommand extends Command {

    public VersionCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "version");
        this.createSignature("gibt die aktuelle Version von Polly zurück");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        this.reply(channel, "Polly-Version: " + this.getMyPolly().getPollyVersion());
        return false;
    }

}
