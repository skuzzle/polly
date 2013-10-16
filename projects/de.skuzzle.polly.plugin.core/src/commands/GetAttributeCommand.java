package commands;

import polly.core.MSG;
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
        super(polly, "getattr"); //$NON-NLS-1$
        this.createSignature(MSG.getAttributeSig0Desc.s, 
    		MyPlugin.GET_USER_ATTRIBUTE_PERMISSION,
            new Parameter(MSG.userName.s, Types.USER), 
            new Parameter(MSG.getAttributeSigAttribute.s, Types.STRING));
        this.createSignature(MSG.getAttributeSig1Desc.s, 
    		MyPlugin.GET_ATTRIBUTE_PERMISSION,
    		new Parameter(MSG.getAttributeSigAttribute.s, Types.STRING));
        this.setHelpText(MSG.getAttributeHelp.s);
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
            throw new CommandException(MSG.unknownUser.s(userName));
        }
        
        try {
            this.reply(channel, MSG.getAttributeValue.s( 
                dest.getAttribute(attribute).valueString(this.getMyPolly().formatting())));
        } catch (UnknownAttributeException e) {
            throw new CommandException(MSG.getAttributeUnknownAttr.s(attribute));
        }
    }
}
