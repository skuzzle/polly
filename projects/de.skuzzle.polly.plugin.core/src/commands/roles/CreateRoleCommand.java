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


public class CreateRoleCommand extends Command {

    
    public CreateRoleCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "createrole"); //$NON-NLS-1$
        this.createSignature(
            MSG.createRoleSig0Desc, 
            MyPlugin.CREATE_ROLE_PERMISSION,
            new Parameter(MSG.createRoleSig0Name, Types.STRING));
        
        this.createSignature(
            MSG.createRoleSig1Desc, 
            MyPlugin.CREATE_ROLE_PERMISSION, 
            new Parameter(MSG.createRoleSig1Base, Types.STRING),
            new Parameter(MSG.createRoleSig1Name, Types.STRING));
        
        this.setHelpText(MSG.createRoleHelp);
    }
    

    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String newRoleName = signature.getStringValue(0);
            
            try {
                this.getMyPolly().roles().createRole(newRoleName);
                this.reply(channel, MSG.createRoleSuccess);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        } else if (this.match(signature, 1)) {
            String baseRoleName = signature.getStringValue(0);
            String newRoleName = signature.getStringValue(1);
            
            try {
                this.getMyPolly().roles().createRole(baseRoleName, newRoleName);
                this.reply(channel, MSG.createRoleSuccess);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (RoleException e) {
                throw new CommandException(e);
            }            
        }
        
        return false;
    }
}
