package commands.roles;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.exceptions.RoleException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;


public class RemoveRoleCommand extends Command {
    
    
    public RemoveRoleCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "removerole");
        this.createSignature(
            "Entfernt eine Rolle von einem Benutzer", 
            RoleManager.ADMIN_PERMISSION, 
            new Parameter("Benutzer", Types.USER),
            new Parameter("Rolle", Types.STRING));
        
        this.setHelpText("Entfernt eine Rolle von einem Benutzer");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String roleName = signature.getStringValue(1);
            
            User user = this.getMyPolly().users().getUser(userName);
            if (user == null) {
                throw new CommandException("Unbekannter Benutzer: " + userName);
            }
            
            try {
                this.getMyPolly().roles().removeRole(user, roleName);
                this.reply(channel, "Rolle erfolgreich zugewiesen");
            } catch (RoleException e) {
                throw new CommandException(e);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }

}
