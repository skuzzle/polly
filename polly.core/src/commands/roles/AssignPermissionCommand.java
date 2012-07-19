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


public class AssignPermissionCommand extends Command {

    public AssignPermissionCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "assignpermission");
        this.createSignature(
            "Fügt einer Rolle eine weitere Berechtigung hinzu", 
            RoleManager.ADMIN_PERMISSION, 
            new Parameter("Rollenname", Types.STRING), 
            new Parameter("Permission", Types.STRING));
        this.setHelpText("Fügt einer Rolle eine weitere Berechtigung hinzu.");
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String roleName = signature.getStringValue(0);
            String permissionname = signature.getStringValue(1);
            
            try {
                this.getMyPolly().roles().assignPermission(roleName, permissionname);
                this.reply(channel, "Berechtigung erfolgreich zugewiesen");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (RoleException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
