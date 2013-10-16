package commands.roles;

import java.util.Iterator;

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


public class ListPermissionsCommand extends Command {

    
    
    public ListPermissionsCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "listpermissions"); //$NON-NLS-1$
        this.createSignature(
            MSG.listPermSig0Desc.s,
            MyPlugin.LIST_PERMISSIONS_PERMISSION,
            new Parameter(MSG.listPermSig0Name.s, Types.STRING));
        
        this.setHelpText(MSG.listPermHelp.s);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String roleName = signature.getStringValue(0);
            
            if (!this.getMyPolly().roles().roleExists(roleName)) {
                throw new CommandException(MSG.bind(MSG.listPermUnknownRole.s, roleName));
            }
            
            StringBuilder result = new StringBuilder();
            Iterator<String> it = 
                this.getMyPolly().roles().getPermissions(roleName).iterator();
            
            while (it.hasNext()) {
                result.append(it.next());
                if (it.hasNext()) {
                    result.append(", "); //$NON-NLS-1$
                }
            }
            this.reply(channel, result.toString());
        }
        
        return false;
    }
}
