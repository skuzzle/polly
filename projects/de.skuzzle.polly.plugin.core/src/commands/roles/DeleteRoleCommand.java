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


public class DeleteRoleCommand extends Command {

    
    public DeleteRoleCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "deleterole"); //$NON-NLS-1$
        this.createSignature(
            MSG.deleteRoleSig0Desc.s,
            MyPlugin.DELETE_ROLE_PERMISSION,
            new Parameter(MSG.deleteRoleSig0Name.s, Types.STRING));
        
        this.setHelpText(MSG.deleteRoleHelp.s);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String roleName = signature.getStringValue(0);
            
            try {
                this.getMyPolly().roles().deleteRole(roleName);
                this.reply(channel, MSG.deleteRoleSuccess.s);
            } catch (RoleException e) {
                throw new CommandException(e);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }
}
