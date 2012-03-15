package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;



public class SetMyPasswordCommand extends Command {

    public SetMyPasswordCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "setmypw");
        this.createSignature("Setzt dein Passwort neu. Gib dein altes Passwort und " +
        		"den gewünschtes Passwort an.", 
        		new Parameter("Altes Passwort", new StringType()), 
        		new Parameter("Neues Passwort", new StringType()));
        this.setRegisteredOnly();
        this.setHelpText("Befehl um dein Passwort zu ändern.");
        this.setQryCommand(true);
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
            String oldPw = signature.getStringValue(0);
            String newPw = signature.getStringValue(1);
            
            if (!executer.checkPassword(oldPw)) {
                this.reply(executer, "Das aktuelle Passwort stimmt nicht mit dem " +
                		"angegebenen Ã¼berein!");
                return;
            }
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            try {
                persistence.writeLock();
                persistence.startTransaction();
                executer.setPassword(newPw);
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
