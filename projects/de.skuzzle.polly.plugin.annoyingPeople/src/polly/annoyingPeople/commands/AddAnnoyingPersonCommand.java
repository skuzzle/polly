package polly.annoyingPeople.commands;

import polly.annoyingPeople.MSG;
import polly.annoyingPeople.MyPlugin;
import polly.annoyingPeople.PersonManager;
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


public class AddAnnoyingPersonCommand extends Command {

    private final PersonManager personManager;
    
    public AddAnnoyingPersonCommand(MyPolly polly, PersonManager personManager) 
            throws DuplicatedSignatureException {
        super(polly, "addAnnoying"); //$NON-NLS-1$
        this.createSignature(MSG.addPersonSig0Desc, 
                MyPlugin.PERMISSION_ADD_ANNOYING_PERSON,
                new Parameter(MSG.addPersongSig0Name, Types.STRING), 
                new Parameter(MSG.addPersonSig0Channel, Types.CHANNEL));
        this.setHelpText(MSG.addPersonHelp);
     
        this.personManager = personManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final String person = signature.getStringValue(0);
            final String c = signature.getStringValue(1);
            try {
                this.personManager.addAnnoyingPerson(person, c);
                this.reply(channel, MSG.bind(MSG.addPersonSuccess, person, c));
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }      
        }
        
        return super.executeOnBoth(executer, channel, signature);
    }
}
