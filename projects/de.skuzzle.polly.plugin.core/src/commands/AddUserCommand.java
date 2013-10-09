package commands;

import polly.core.Messages;
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
import de.skuzzle.polly.sdk.exceptions.InvalidUserNameException;
import de.skuzzle.polly.sdk.exceptions.UserExistsException;

public class AddUserCommand extends Command {
    
    public AddUserCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "adduser");
        this.createSignature(Messages.addUserSig0Desc, 
            MyPlugin.ADD_USER_PERMISSION,
            new Parameter(Messages.userName, Types.USER),
            new Parameter(Messages.addUserSig0Password, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(Messages.addUserHelp);
        this.setQryCommand(true);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }

    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        this.reply(channel, Messages.addUserQryOnly);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) 
                throws CommandException {
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String password = signature.getStringValue(1);
            
            try {
                this.getMyPolly().users().addUser(userName, password);
                this.reply(executer, Messages.bind(Messages.addUserSuccess, userName));
            } catch (UserExistsException e) {
                this.reply(executer, Messages.bind(Messages.addUserExists, userName));
            } catch (DatabaseException e)  {
                throw new CommandException(e);
            } catch (InvalidUserNameException e) {
                this.reply(executer, Messages.bind(Messages.addUserInvalid, userName));
            }
        }
    }
}
