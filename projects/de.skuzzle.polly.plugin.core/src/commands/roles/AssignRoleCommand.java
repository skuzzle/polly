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


public class AssignRoleCommand extends Command {

    public AssignRoleCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "assignrole"); //$NON-NLS-1$
        this.createSignature(
            MSG.assignRoleSig0Desc, 
            MyPlugin.ASSIGN_ROLE_PERMISSION, 
            new Parameter(MSG.assignRoleSig0User, Types.USER),
            new Parameter(MSG.assignRoleSig0Role, Types.STRING));
        this.setHelpText(MSG.assignRoleHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String roleName = signature.getStringValue(1);
            
            User user = this.getMyPolly().users().getUser(userName);
            if (user == null) {
                throw new CommandException(MSG.bind(MSG.assignRoleUnknownUser, userName));
            }
            
            try {
                this.getMyPolly().roles().assignRole(user, roleName);
                this.reply(channel, MSG.assignRoleSuccess);
            } catch (RoleException e) {
                throw new CommandException(e);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }

}
