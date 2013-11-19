package commands;

import java.util.Collections;
import java.util.List;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.ChannelType;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class JoinCommand extends Command {

    
    public JoinCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "join"); //$NON-NLS-1$
        this.createSignature(MSG.joinSig0Desc, 
            MyPlugin.JOIN_PERMISSION,
                new Parameter(MSG.joinSig0Channel, Types.CHANNEL));
        this.createSignature(MSG.joinSig1Desc,
            MyPlugin.JOIN_PERMISSION,
                new Parameter(MSG.joinSig1Channels, new ListType(Types.CHANNEL)));
        this.createSignature(MSG.joinSig2Desc,
            MyPlugin.JOIN_PERMISSION,
            new Parameter(MSG.joinSig2Channel, Types.CHANNEL),
            new Parameter(MSG.joinSig2Password, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.joinHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {

        List<ChannelType> channels = null;
        if (this.match(signature, 0)) {
            ChannelType ct = new ChannelType(signature.getStringValue(0));
            channels = Collections.singletonList(ct);
        } else if (this.match(signature, 1)) {
            channels = signature.getListValue(ChannelType.class, 0);
        } else if (this.match(signature, 2)) {
            String c = signature.getStringValue(0);
            String pw = signature.getStringValue(1);
            
            this.getMyPolly().irc().joinChannel(c, pw);
            return false;
        }
        
        if (channels == null) {
            throw new RuntimeException(
                "This should not have happened. Command was called with illegal signature"); //$NON-NLS-1$
        }
        
        for (ChannelType ct : channels) {
            this.getMyPolly().irc().joinChannel(ct.getValue(), ""); //$NON-NLS-1$
        }
        
        return false;
    }

}
