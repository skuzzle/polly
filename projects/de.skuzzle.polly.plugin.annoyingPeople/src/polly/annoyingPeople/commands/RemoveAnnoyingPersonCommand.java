package polly.annoyingPeople.commands;

import polly.annoyingPeople.MSG;
import polly.annoyingPeople.MyPlugin;
import polly.annoyingPeople.core.PersonManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class RemoveAnnoyingPersonCommand extends Command {

    private final PersonManager personManager;
    
    public RemoveAnnoyingPersonCommand(MyPolly polly, PersonManager personManager) 
            throws DuplicatedSignatureException {
        super(polly, "removeAnnoying"); //$NON-NLS-1$
        this.createSignature(MSG.removePersonSig0Desc, 
                MyPlugin.PERMISSION_ADD_ANNOYING_PERSON,
                new Parameter(MSG.removePersongSig0Name, Types.STRING), 
                new Parameter(MSG.removePersonSig0Channel, Types.CHANNEL));
        this.setHelpText(MSG.removePersonHelp);
     
        this.personManager = personManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final String person = signature.getStringValue(0);
            final String c = signature.getStringValue(1);
            try {
                this.personManager.removeAnnoyingPerson(person, c);
                this.reply(channel, MSG.bind(MSG.removePersonSuccess, person, c));
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }      
        }
        
        return super.executeOnBoth(executer, channel, signature);
    }
}
