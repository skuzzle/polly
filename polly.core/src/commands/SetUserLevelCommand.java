package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class SetUserLevelCommand extends Command {

    public SetUserLevelCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "setlevel");
        this.createSignature("Ä„ndert das user-Level des angegebenen Benutzers", 
                new UserType(), new NumberType());
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Befehl zum Ändern von Benutzerberechtigungen.");
    }

    
    @Override
    public void renewConstants() {
        this.registerConstant("ADMIN", new NumberType(UserManager.ADMIN));
        this.registerConstant("MEMBER", new NumberType(UserManager.MEMBER));
        this.registerConstant("REG", new NumberType(UserManager.REGISTERED));
        this.registerConstant("UNKNOWN", new NumberType(UserManager.UNKNOWN));
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {

        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            int newLevel = (int) signature.getNumberValue(1);
            
            User u = this.getMyPolly().users().getUser(userName);
            if (u == null) {
                this.reply(channel, "Benutzer '" + userName + "' existiert nicht.");
                return false;
            }
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            try {
                persistence.writeLock();
                persistence.startTransaction();
                u.setUserLevel(newLevel);
                persistence.commitTransaction();
                this.reply(channel, "User-level wurde erfolgreich geändert.");
            } catch (DatabaseException e) {
                this.reply(channel, "Interner Datenbankfehler.");
            } finally {
                persistence.writeUnlock();
            }
        }
        return false;
    }
}
