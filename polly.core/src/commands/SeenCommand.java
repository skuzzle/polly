package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class SeenCommand extends Command {

    public SeenCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "seen");
        this.createSignature("Zeigt an wann ein Benutzer das letzte mal gesehen wurde.", 
            new UserType());
        this.setHelpText("Zeigt an wann ein Benutzer das letzte mal gesehen wurde.");
    }

    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String user = signature.getStringValue(0);
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            User u = this.getMyPolly().users().getUser(new IrcUser(user, "", ""));
            if (u != null) {
                
            }
        }
        return false;
    }
}
