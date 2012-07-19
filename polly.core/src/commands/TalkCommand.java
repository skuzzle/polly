package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class TalkCommand extends Command {

    public TalkCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "talk");
        this.createSignature("Spricht zum aktuellen Channel", 
            MyPlugin.TALK_PERMISSION,
            new Parameter("Nachricht", Types.STRING));
        this.createSignature("Spricht zum angegebenen Channel",
            MyPlugin.TALK_PERMISSION,
            new Parameter("Channel", Types.CHANNEL), 
            new Parameter("Nachricht", Types.STRING));
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
