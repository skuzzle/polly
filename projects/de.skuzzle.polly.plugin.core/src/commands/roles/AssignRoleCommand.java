package commands.roles;

import polly.core.MyPlugin;
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


public class AssignRoleCommand extends Command {

    public AssignRoleCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "assignrole");
        this.createSignature(
            "Weist einem Benutzer eine neue Rolle zu", 
            MyPlugin.ASSIGN_ROLE_PERMISSION, 
            new Parameter("Benutzer", Types.USER),
            new Parameter("Rolle", Types.STRING));
        
        this.setHelpText("Weist einem Benutzer eine neue Rolle zu");
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
                this.getMyPolly().roles().assignRole(user, roleName);
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
