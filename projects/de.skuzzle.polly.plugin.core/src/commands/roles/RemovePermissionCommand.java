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


public class RemovePermissionCommand extends Command {

    public RemovePermissionCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "removepermission"); //$NON-NLS-1$
        this.createSignature(
            MSG.removePermSig0Desc.s, 
            MyPlugin.REMOVE_PERMISSION_PERMISSION, 
            new Parameter(MSG.removePermSig0Role.s, Types.STRING), 
            new Parameter(MSG.removePermSig0Perm.s, Types.STRING));
        this.setHelpText(MSG.removePermHelp.s);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String roleName = signature.getStringValue(0);
            String permissionname = signature.getStringValue(1);
            
            try {
                this.getMyPolly().roles().removePermission(roleName, permissionname);
                this.reply(channel, MSG.removePermSuccess.s);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (RoleException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
