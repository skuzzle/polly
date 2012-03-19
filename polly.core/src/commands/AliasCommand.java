package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.model.User;



public class AliasCommand extends Command {

    public AliasCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "alias");
        this.createSignature("Erstellt ein eines mit dem angegebenen Namen für den" +
            " angegebenen Befehl", 
            new Parameter("Alias", Types.STRING),
            new Parameter("Befehl", Types.COMMAND));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String alias = signature.getStringValue(0);
            String cmdName = signature.getStringValue(1);
            
            try {
                Command cmd = this.getMyPolly().commands().getCommand(cmdName);
                this.getMyPolly().commands().registerCommand(alias, cmd);
                this.reply(channel, "Alias '" + alias + "' für den Befehl '" + 
                    cmdName + "' angelegt.");
            } catch (UnknownCommandException e) {
                this.reply(channel, "Befehl '" + cmdName + "' unbekannt.");
            } catch (DuplicatedSignatureException e) {
                this.reply(channel, "Fehler beim Anlegen des Alias.");
            }
            
        }
        return false;
    }

}
