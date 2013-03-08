package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class SetPasswordCommand extends Command {

    public SetPasswordCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "setpw");
        this.createSignature("Setzt das Passwort eines Benutzers neu.",
                MyPlugin.SET_PASSWORD_PERMISSION,
        		new Parameter("User", Types.USER), 
        		new Parameter("Passwort", Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText("Befehl um das Passwort eines Benutzers zu ändern.");
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        this.reply(channel, "Dieser Befehl ist nur im Query ausführbar. " +
        		"Du solltest zudem ein anderes Passwort wählen.");
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String newPw = signature.getStringValue(1);
            
            User u = this.getMyPolly().users().getUser(userName);
            if (u == null) {
                this.reply(executer, "Benutzer '" + userName + "' existiert nicht.");
                return;
            }
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            try {
                persistence.writeLock();
                persistence.startTransaction();
                u.setPassword(newPw);
                persistence.commitTransaction();
                this.reply(executer, "Dein Passwort wurde erfolgreich geändert.");
            } catch (DatabaseException e) {
                this.reply(executer, "Interner Datenbankfehler.");
            } finally {
                persistence.writeUnlock();
            }
        }
    }
}
