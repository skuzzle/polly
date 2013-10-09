package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;

public class DeleteUserCommand extends Command {

    public DeleteUserCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "deluser"); //$NON-NLS-1$
        this.createSignature(MSG.deleteUserSig0Desc, 
            MyPlugin.DELETE_USER_PERMISSION,
            new Parameter(MSG.userName, Types.USER));
        this.setRegisteredOnly();
        this.setHelpText(MSG.deleteUserHelp);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
     
        if (this.match(signature, 0)) {
            String name = signature.getStringValue(0);
            UserManager um = this.getMyPolly().users();
            
            User user = um.getUser(name);
            if (user == null) {
                this.reply(channel, MSG.bind(MSG.unknownUser, name));
                return false;
            }
            try {
                this.getMyPolly().users().deleteUser(user);
                this.reply(channel, MSG.bind(MSG.deleteUserSuccess, name));
            } catch (UnknownUserException ignore) {
                // can not happen
                ignore.printStackTrace();
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
