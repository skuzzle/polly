package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManager;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.WriteAction;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class GreetingCommand extends Command {

    public GreetingCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "greeting");
        this.createSignature("Erstellt eine Begrüßung.", new Types.StringType());
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(final User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            final String greet = signature.getStringValue(0);
            
            PersistenceManager persistence = this.getMyPolly().persistence();
            
            try {
                persistence.atomicWriteOperation(new WriteAction() {
                    @Override
                    public void performUpdate(PersistenceManager persistence) {
                        executer.setAttribute(MyPlugin.GREETING, greet);
                    }
                });
                this.reply(channel, "Grußnachricht gespeichert.");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }
}
