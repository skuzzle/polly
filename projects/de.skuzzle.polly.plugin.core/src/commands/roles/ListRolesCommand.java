package commands.roles;

import java.util.Iterator;
import java.util.Set;

import polly.core.MyPlugin;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class ListRolesCommand extends Command {

    public final static String LIST_ROLES_PERMISSION = "polly.permission.LIST_ROLES";
    
    public ListRolesCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "listroles");
        this.createSignature(
            "Listet alle Rollen eines Benutzers auf",
            MyPlugin.LIST_ROLES_PERMISSION,
            new Parameter("Benutzer", Types.USER));
        this.createSignature(
            "Listet alle verfügbaren Rollen auf",
            MyPlugin.LIST_ROLES_PERMISSION);
        this.setHelpText("Listet verfügbare Benutzer-Rollen auf");
    }


    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        Set<String> roles = null;
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            
            User user = this.getMyPolly().users().getUser(userName);
            
            if (user == null) {
                throw new CommandException("Unbekannter Benutzer: " + userName);
            }
            
            roles = this.getMyPolly().roles().getRoles(user);
        } else if (this.match(signature, 1)) {
            roles = this.getMyPolly().roles().getRoles();
        }
        
        
        Iterator<String> it = roles.iterator();
        StringBuilder result = new StringBuilder();
        while (it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(", ");
            }
        }
        this.reply(channel, result.toString());
        
        return false;
    }
}
