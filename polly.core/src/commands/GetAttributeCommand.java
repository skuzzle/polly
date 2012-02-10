package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;
import de.skuzzle.polly.sdk.model.User;

public class GetAttributeCommand extends Command {

    public GetAttributeCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "getattr");
        this.createSignature("", new UserType(), new StringType());
        this.createSignature("", new StringType());
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            if (executer.getUserLevel() < UserManager.ADMIN) {
                throw new CommandException(
                    "Du kannst keine Attribute für andere Benutzer ändern");
            }
            
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
            throw new CommandException("Unbekannter Benutzer: " + userName);
        }
        
        try {
            this.reply(channel, "Attributwert: " + dest.getAttribute(attribute));
        } catch (UnknownAttributeException e) {
            throw new CommandException("Unbekanntes Attribut: " + attribute);
        }
    }
}
