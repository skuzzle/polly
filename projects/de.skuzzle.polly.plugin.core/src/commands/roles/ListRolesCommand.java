package commands.roles;

import java.util.Iterator;
import java.util.Set;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class ListRolesCommand extends Command {

    public final static String LIST_ROLES_PERMISSION = "polly.permission.LIST_ROLES"; //$NON-NLS-1$
    
    public ListRolesCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "listroles"); //$NON-NLS-1$
        this.createSignature(
            MSG.listRolesSig0Desc.s,
            MyPlugin.LIST_ROLES_PERMISSION,
            new Parameter(MSG.listRolesSig0User.s, Types.USER));
        this.createSignature(
            MSG.listRolesSig1Desc.s,
            MyPlugin.LIST_ROLES_PERMISSION);
        this.setHelpText(MSG.listRolesHelp.s);
    }


    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        Set<String> roles = null;
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            
            User user = this.getMyPolly().users().getUser(userName);
            
            if (user == null) {
                throw new CommandException(MSG.listRolesUnknownUser.s(userName));
            }
            
            roles = this.getMyPolly().roles().getRoles(user);
        } else if (this.match(signature, 1)) {
            roles = this.getMyPolly().roles().getRoles();
        } else {
            throw new IllegalStateException("wrong signature passed to command"); //$NON-NLS-1$
        }
        
        
        Iterator<String> it = roles.iterator();
        StringBuilder result = new StringBuilder();
        while (it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(", "); //$NON-NLS-1$
            }
        }
        this.reply(channel, result.toString());
        
        return false;
    }
}
