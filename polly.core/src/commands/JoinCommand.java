package commands;

import java.util.Collections;
import java.util.List;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.ChannelType;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class JoinCommand extends Command {

    
    
    public JoinCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "join");
        this.createSignature("Lässt polly den angegebenen Channel betreten.", 
                new Parameter("Channel", Types.newChannel()));
        this.createSignature("Lässt polly alle Channels in der Liste betreten.", 
                new Parameter("Channelliste", new ListType(Types.newChannel())));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Befehl zum joinen von Channels.");
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
        }
        
        for (ChannelType ct : channels) {
            this.getMyPolly().irc().joinChannel(ct.getValue(), "");
        }
        
        return false;
    }

}
