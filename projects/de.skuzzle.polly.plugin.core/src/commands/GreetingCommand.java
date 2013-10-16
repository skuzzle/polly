package commands;

import polly.core.MSG;
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
        this.createSignature(MSG.greetingSig0Desc.s, 
            new Parameter(MSG.greetingSig0Greeting.s, Types.STRING));
        this.setHelpText(MSG.greetingHelp.s);
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
                this.reply(channel, MSG.greetingStored.s);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (ConstraintException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
