package commands;

import polly.core.Messages;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class GreetingCommand extends Command {

    public GreetingCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "greeting"); //$NON-NLS-1$
        this.createSignature(Messages.greetingSig0Desc, 
            new Parameter(Messages.greetingSig0Greeting, Types.STRING));
        this.setHelpText(Messages.greetingHelp);
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(final User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            final String greet = signature.getStringValue(0);
            
            try {
                getMyPolly().users().setAttributeFor(executer, executer, 
                    MyPlugin.GREETING, greet);
                this.reply(channel, Messages.greetingStored);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (ConstraintException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
