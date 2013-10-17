package commands;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class UsersCommand extends Command {

    
    public UsersCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "users"); //$NON-NLS-1$
        this.createSignature(MSG.usersSig0Desc,
            MyPlugin.LIST_USERS_PERMISSION);
        this.createSignature(MSG.usersSig1Desc,
        		MyPlugin.LIST_USERS_PERMISSION,
    		new Parameter(MSG.usersSig1Pattern, Types.STRING));
        this.createSignature(MSG.usersSig2Desc,
    		MyPlugin.LIST_USERS_PERMISSION,
    		new Parameter(MSG.usersSig2Pattern, Types.STRING), 
    		new Parameter(MSG.usersSig2LoggedInOnly, Types.BOOLEAN));
        this.setRegisteredOnly();
        this.setHelpText(MSG.usersHelp);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        String pattern =".*"; //$NON-NLS-1$
        boolean loggedInOnly = false;
        
        if (this.match(signature, 1)) {
            pattern = signature.getStringValue(0);
        } else if (this.match(signature, 2)) {
            pattern = signature.getStringValue(0);
            loggedInOnly = signature.getBooleanValue(1);
        }
        
        List<User> users = this.getMyPolly().users().getRegisteredUsers();
        Collections.sort(users);
        
        Pattern p = Pattern.compile(pattern.toLowerCase());
        StringBuilder b = new StringBuilder();
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            User current = it.next();
            this.getMyPolly().persistence().refresh(current);
            
            Matcher m1 = p.matcher(current.getName().toLowerCase());
            String cNick = current.getCurrentNickName();
            cNick = cNick == null ? "" : cNick.toLowerCase(); //$NON-NLS-1$
            Matcher m2 = p.matcher(cNick);
            
            if (m1.matches() || m2.matches()) {
                if (loggedInOnly && !this.getMyPolly().users().isSignedOn(current)) {
                    continue;
                }

                b.append(current.getName());
                if (this.getMyPolly().users().isSignedOn(current)) {
                    b.append(" ("); //$NON-NLS-1$
                    b.append(current.getCurrentNickName());
                    b.append(")"); //$NON-NLS-1$
                }
                
                if (it.hasNext()) {
                    b.append(", "); //$NON-NLS-1$
                }
            }
        }
        
        if (users.isEmpty()) {
            b.append(MSG.usersNoUsers);
        }
        
        this.reply(channel, b.toString());

        return false;
    }
}
