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



public class HopCommand extends Command {

    public HopCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "hop"); //$NON-NLS-1$
        this.createSignature(MSG.hopSig0Desc.s,
            MyPlugin.HOP_PERMISSION);
        this.createSignature(MSG.hopSig1Desc.s,
            MyPlugin.HOP_PERMISSION,
            new Parameter(MSG.hopSig1Channel.s, Types.CHANNEL));
        this.setRegisteredOnly();
        this.setHelpText(MSG.hopHelp.s);
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
            this.reply(executer, MSG.hopSpecifyChannel.s);
        } else if (this.match(signature, 1)) {
            this.rejoin(signature.getStringValue(0));
        }
    }
    
    
    
    private void rejoin(String channel) {
        this.getMyPolly().irc().partChannel(channel, MSG.hopPartMessage.s);
        this.getMyPolly().irc().joinChannel(channel, ""); //$NON-NLS-1$
    }
}
