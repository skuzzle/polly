package commands;

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
        this.createSignature("Liest den Wert eines Attributes eine Benutzers aus. " +
        		"Dieser Befehl ist nur für Admins", 
    		MyPlugin.GET_USER_ATTRIBUTE_PERMISSION,
            new Parameter("Benutzer", Types.USER), 
            new Parameter("Attributename", Types.STRING));
        this.createSignature("Liest ein Attribute des Benutzers aus, der den Befehl " +
        		"ausführt.", 
    		MyPlugin.GET_ATTRIBUTE_PERMISSION,
    		new Parameter("Attributname", Types.STRING));
        this.setHelpText("Liest Benutzer-Attribute aus. Verfügbare Attribute können " +
        		"mit :listattr angezeigt werden.");
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
            throw new CommandException("Unbekannter Benutzer: " + userName);
        }
        
        try {
            this.reply(channel, "Attributwert: " + dest.getAttribute(attribute));
        } catch (UnknownAttributeException e) {
            throw new CommandException("Unbekanntes Attribut: " + attribute);
        }
    }
}
