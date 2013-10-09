package commands;

import polly.core.Messages;
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



public class RegisterCommand extends Command {

    public RegisterCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "register"); //$NON-NLS-1$
        this.createSignature(Messages.registerSig0Desc, 
    		new Parameter(Messages.registerSig0Password, Types.STRING));
        this.setHelpText(Messages.registerHelp);
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
        this.reply(channel, Messages.registerQryWarning);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) throws CommandException {
        if (this.getMyPolly().users().isSignedOn(executer)) {
            this.reply(executer, Messages.registerAlreadySignedOn);
            return;
        }
        
        String userName = ""; //$NON-NLS-1$
        String password = ""; //$NON-NLS-1$
        if (this.match(signature, 0)) {
            userName = executer.getCurrentNickName();
            password = signature.getStringValue(0);
        }
        try {
            this.getMyPolly().users().addUser(
                    userName, password);
            this.reply(executer, Messages.bind(
                    Messages.registerSuccess, userName, password));
        } catch (UserExistsException e) {
            this.reply(executer, Messages.bind(Messages.registerAlreadyExists, userName));
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new CommandException(e);
        } catch (InvalidUserNameException e) {
            this.reply(executer, Messages.bind(Messages.registerInvalidName, userName));
        }
    }
}
