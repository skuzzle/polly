package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class InspectCommand extends Command {

    public InspectCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "inspect");
        this.createSignature("Untersucht eine Deklaration des angegebenen Benutzers", new UserType(), new StringType());
        this.createSignature("Untersucht eine Deklaration", new StringType());
        this.setHelpText("Dieser Befehl gibt den Typ einer Deklaration aus.");
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String name = signature.getStringValue(0);
            User u = this.getMyPolly().users().getUser(name);
            if (u == null) {
                this.reply(channel, "Unbekannter User/Namespace: " + name);
            }
            this.inspect(channel, u, signature.getStringValue(1));
        } else if (this.match(signature, 1)) {
            this.inspect(channel, executer, signature.getStringValue(0));
        }
        
        return false;
    }
    
    
    
    private void inspect(String channel, User user, String declaration) {
        String i = this.getMyPolly().users().inspect(user, declaration);
        if (i == null) {
            this.reply(channel, "Unbekannte Deklaration: " + declaration);
            return;
        }
        this.reply(channel, i);
    }
}
