package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.ChannelType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class TalkCommand extends Command {

    public TalkCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "talk");
        this.createSignature("Spricht zum aktuellen Channel", 
            new Parameter("Nachricht", new StringType()));
        this.createSignature("Spricht zum angegebenen Channel", 
            new Parameter("Channel", new ChannelType()), 
            new Parameter("Nachricht", new StringType()));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        if (this.match(signature, 0)) {
            String m = signature.getStringValue(0);
            this.reply(channel, m);
        } else if (this.match(signature, 1)) {
            String c = signature.getStringValue(0);
            String m = signature.getStringValue(1);
            this.reply(c, m);
        }
    }
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        if (this.match(signature, 1)) {
            String c = signature.getStringValue(0);
            String m = signature.getStringValue(1);
            this.reply(c, m);
        }
    }
}
