package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;

public class DeleteUserCommand extends Command {

    public DeleteUserCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "deluser");
        this.createSignature("L�scht den angegebenen Benutzer.", 
            MyPlugin.DELETE_USER_PERMISSION,
            new Parameter("User", Types.USER));
        this.setRegisteredOnly();
        this.setHelpText("Befehl zum L�schen von Benutzern.");
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
                this.reply(channel, "Benutzer '" + name + "' wurde gel�scht.");
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
