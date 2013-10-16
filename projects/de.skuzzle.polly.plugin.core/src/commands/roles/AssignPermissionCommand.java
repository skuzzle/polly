package commands.roles;

import polly.core.MSG;
import polly.core.MyPlugin;
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
import de.skuzzle.polly.sdk.exceptions.RoleException;


public class AssignPermissionCommand extends Command {

    public AssignPermissionCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "assignpermission"); //$NON-NLS-1$
        this.createSignature(MSG.assignPermSig0Desc.s, 
            MyPlugin.ASSIGN_PERMISSION_PERMISSION, 
            new Parameter(MSG.assignPermSig0Role.s, Types.STRING), 
            new Parameter(MSG.assignPermSig0Perm.s, Types.STRING));
        this.setHelpText(MSG.assignPermHelp.s);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String roleName = signature.getStringValue(0);
            String permissionname = signature.getStringValue(1);
            
            try {
                this.getMyPolly().roles().assignPermission(roleName, permissionname);
                this.reply(channel, MSG.assignPermSuccess.s);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (RoleException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
