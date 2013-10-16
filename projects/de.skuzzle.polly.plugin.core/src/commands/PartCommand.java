package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class PartCommand extends Command {

    public PartCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "part"); //$NON-NLS-1$
        this.createSignature(MSG.partSig0Desc.s, 
            MyPlugin.PART_PERMISSION);
        this.createSignature(MSG.partSig1Desc.s,
                MyPlugin.PART_PERMISSION,
                new Parameter(MSG.partSig1Channel.s, Types.CHANNEL));
        this.setRegisteredOnly();
        this.setHelpText(MSG.partHelp.s);
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
            this.reply(executer, MSG.partSpecifyChannel.s);
        } else if (this.match(signature, 1)) {
            this.part(signature.getStringValue(0));
        }
    }
    
    
    
    private void part(String channel) {
        this.getMyPolly().irc().partChannel(channel, MSG.partMessage.s);
    }
}
