package commands;

import polly.core.Messages;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;

public class GetAttributeCommand extends Command {

    public GetAttributeCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "getattr");
        this.createSignature(Messages.getAttributeSig0Desc, 
    		MyPlugin.GET_USER_ATTRIBUTE_PERMISSION,
            new Parameter(Messages.userName, Types.USER), 
            new Parameter(Messages.getAttributeSigAttribute, Types.STRING));
        this.createSignature(Messages.getAttributeSig1Desc, 
    		MyPlugin.GET_ATTRIBUTE_PERMISSION,
    		new Parameter(Messages.getAttributeSigAttribute, Types.STRING));
        this.setHelpText(Messages.getAttributeHelp);
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String user = signature.getStringValue(0);
            String attribute = signature.getStringValue(1);
            
            User dest = this.getMyPolly().users().getUser(user);
            this.getAttribute(dest, user, attribute, channel);
        } else if (this.match(signature, 1)) {
            String attribute = signature.getStringValue(0);
            
            this.getAttribute(executer, executer.getName(), attribute, channel);
        }
        return false;
    }
    
    
    
    private void getAttribute(User dest, String userName, String attribute, String channel) 
            throws CommandException {
        if (dest == null) {
            throw new CommandException(Messages.bind(Messages.unknownUser, userName));
        }
        
        try {
            this.reply(channel, Messages.bind(Messages.getAttributeValue, 
                dest.getAttribute(attribute).valueString(this.getMyPolly().formatting())));
        } catch (UnknownAttributeException e) {
            throw new CommandException(
                    Messages.bind(Messages.getAttributeUnknownAttr, attribute));
        }
    }
}
