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


public class RemoveRoleCommand extends Command {
    
    
    public RemoveRoleCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "removerole"); //$NON-NLS-1$
        this.createSignature(
            MSG.removeRoleSig0Desc, 
            MyPlugin.REMOVE_ROLE_PERMISSION, 
            new Parameter(MSG.removeRoleSig0User, Types.USER),
            new Parameter(MSG.removeRoleSig0Role, Types.STRING));
        
        this.setHelpText(MSG.removeRoleHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String roleName = signature.getStringValue(1);
            
            User user = this.getMyPolly().users().getUser(userName);
            if (user == null) {
                throw new CommandException(MSG.bind(MSG.removeRoleUnknownUser, userName));
            }
            
            try {
                this.getMyPolly().roles().removeRole(user, roleName);
                this.reply(channel, MSG.removeRoleSuccess);
            } catch (RoleException e) {
                throw new CommandException(e);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }

}
