package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class GreetingCommand extends Command {

    public GreetingCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "greeting");
        this.createSignature("Erstellt eine Begrüßung.", 
            new Parameter("Grußnachricht", new StringType()));
        this.setHelpText("Eine Begrüßung wird dir zugestellt sobald du dich bei polly " +
        		"anmeldest");
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(final User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            final String greet = signature.getStringValue(0);
            
            try {
                getMyPolly().users().setAttributeFor(executer, MyPlugin.GREETING, greet);
                
                this.reply(channel, "Grußnachricht gespeichert.");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (ConstraintException e) {
                this.reply(channel, "Wert konnte nicht gesetzt werden.");
            }
        }
        
        return false;
    }
}
