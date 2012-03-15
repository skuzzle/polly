package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.model.User;

public class DeleteUserCommand extends Command {

    public DeleteUserCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "deluser");
        this.createSignature("Löscht den angegebenen Benutzer.", 
            new Parameter("User", new UserType()));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Befehl zum Löschen von Benutzern.");
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
     
        if (this.match(signature, 0)) {
            String name = signature.getStringValue(0);
            UserManager um = this.getMyPolly().users();
            
            User user = um.getUser(name);
            if (user == null) {
                this.reply(channel, "Benutzer '" + name + "' existiert nicht.");
                return false;
            }
            try {
                this.getMyPolly().users().deleteUser(user);
                this.reply(channel, "Benutzer '" + name + "' wurde gelöscht.");
            } catch (UnknownUserException ignore) {
                // can not happen
                ignore.printStackTrace();
            } catch (DatabaseException e) {
                this.reply(channel, "Interner Datenbank Fehler: " + e.getMessage());
            }
        }
        return false;
    }
}
