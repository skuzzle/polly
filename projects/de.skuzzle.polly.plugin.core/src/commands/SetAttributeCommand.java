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
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;


public class SetAttributeCommand extends Command {

    public SetAttributeCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "setattr"); //$NON-NLS-1$
        this.createSignature(MSG.setAttributeSig0Desc, 
    		MyPlugin.SET_USER_ATTRIBUTE_PERMISSION,
            new Parameter(MSG.setAttributeSig0User, Types.USER), 
            new Parameter(MSG.setAttributeSig0Attr, Types.STRING), 
            new Parameter(MSG.setAttributeSig0Value, Types.ANY));
        this.createSignature(MSG.setAttributeSig1Desc,
            MyPlugin.SET_ATTRIBUTE_PERMISSION,
            new Parameter(MSG.setAttributeSig1Attr, Types.STRING), 
            new Parameter(MSG.setAttributSig1Value, Types.ANY));
        this.setRegisteredOnly();
        this.setHelpText(MSG.setAttributeHelp);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String user = signature.getStringValue(0);
            final String attribute = signature.getStringValue(1);
            final Types value = signature.getValue(2);
            
            final User dest = this.getMyPolly().users().getUser(user);
            this.setAttribute(executer, dest, user, attribute, 
                value.valueString(this.getMyPolly().formatting()), channel);
            
        } else if (this.match(signature, 1)) {
            final String attribute = signature.getStringValue(0);
            final Types value = signature.getValue(1);
            
            this.setAttribute(executer, executer, executer.getName(), attribute, 
                value.valueString(this.getMyPolly().formatting()), channel);
        }
        return false;
    }
    
    
    
    private void setAttribute(User executor, User dest, String userName, String attribute, 
            String value, String channel) throws CommandException {
        
        if (dest == null) {
            throw new CommandException(MSG.bind(MSG.setAttributeUnknownUser, userName));
        } 
        
        try {
            dest.getAttribute(attribute);
        } catch (UnknownAttributeException e) {
            throw new CommandException(MSG.bind(MSG.setAttributeUnknownAttr, attribute));
        }
        
        try {
            this.getMyPolly().users().setAttributeFor(executor, dest, attribute, value);
            this.reply(channel, MSG.setAttributeSuccess);
        } catch (DatabaseException e) {
            throw new CommandException(e);
        } catch (ConstraintException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
