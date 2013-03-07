package commands;

import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;


public class ToggleMailCommand extends AbstractRemindCommand {

    public ToggleMailCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        
        super(polly, manager, "togglemail");
        this.createSignature("Wandelt eine Erinnerung in eine E-Mail Benachrichtigung " +
        		"um oder umgekehrt", 
    		MyPlugin.TOGGLE_MAIL_PERMISSION,
            new Parameter("Remind Id", Types.NUMBER));
        this.setHelpText("Wandelt eine Erinnerung in eine E-Mail Benachrichtigung um " +
        		"oder umgekehrt");
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            int id = (int) signature.getNumberValue(0);
            try {
                RemindEntity r = this.remindManager.toggleIsMail(executer, id);
                if (r.isMail()) {
                    this.reply(channel, "Typ geändert zu: E-Mail Benachrichtigung.");
                } else {
                    this.reply(channel, "Typ geändert zu: IRC Benachrichtigung.");
                }
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
            
        }
        return false;
    }

}
