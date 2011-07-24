package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;



public class VenadCommand extends Command {

    public VenadCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "venad");
        this.createSignature("Gibt den Venadnamen eines Benutzers zurück.", 
                new UserType());
        this.setHelpText("Befehl zum Rausfinden des Venad-Namens eines registrierten " +
        		"Benutzers.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            User u = this.getMyPolly().users().getUser(userName);
            
            if (u == null) {
                this.reply(channel, "Benutzer mit Namen '" + userName + 
                        "' existiert nicht.");
                return false;
            }
            
            this.reply(channel, "Venad von " + userName + ": " + u.getAttribute("VENAD"));
        }
        return false;
    }
}