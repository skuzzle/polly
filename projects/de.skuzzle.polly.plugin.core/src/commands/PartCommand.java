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



public class PartCommand extends Command {

    public PartCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "part");
        this.createSignature("Polly verlässt den aktuellen Channel", 
            MyPlugin.PART_PERMISSION);
        this.createSignature("Polly verlässt den angegebenen Channel.",
                MyPlugin.PART_PERMISSION,
                new Parameter("Channel", Types.CHANNEL));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Befehl um einen Channel zu verlassen.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        String c = channel;
        if (this.match(signature, 1)) {
            c = signature.getStringValue(0);
        }
        this.part(c);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        if (this.match(signature, 0)) {
            this.reply(executer, "Bitte gib einen Channel an.");
        } else if (this.match(signature, 1)) {
            this.part(signature.getStringValue(0));
        }
    }
    
    
    
    private void part(String channel) {
        this.getMyPolly().irc().partChannel(channel, "byebye");
    }

}
