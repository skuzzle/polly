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



public class HopCommand extends Command {

    public HopCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "hop");
        this.createSignature("Rejoined den aktuellen Channel.",
            MyPlugin.HOP_PERMISSION);
        this.createSignature("Rejoined den angegebenen Channel",
            MyPlugin.HOP_PERMISSION,
            new Parameter("Channel", Types.CHANNEL));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Befehl zum rejoinen von channels.");
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
        this.rejoin(c);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        if (this.match(signature, 0)) {
            this.reply(executer, "Bitte gib einen Channel an.");
        } else if (this.match(signature, 1)) {
            this.rejoin(signature.getStringValue(0));
        }
    }
    
    
    private void rejoin(String channel) {
        this.getMyPolly().irc().partChannel(channel, "rejoining...");
        this.getMyPolly().irc().joinChannel(channel, "");
    }
}
