package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Atomic;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class SetPasswordCommand extends Command {

    public SetPasswordCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "setpw"); //$NON-NLS-1$
        this.createSignature(MSG.setPasswordSig0Desc,
                MyPlugin.SET_PASSWORD_PERMISSION,
        		new Parameter(MSG.setPasswordSig0User, Types.USER), 
        		new Parameter(MSG.setPasswordSig0Password, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.setPasswordHelp);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        this.reply(channel, MSG.setPasswordQryWarnning);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) 
            throws CommandException {
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            final String newPw = signature.getStringValue(1);
            
            final User u = this.getMyPolly().users().getUser(userName);
            if (u == null) {
                this.reply(executer, MSG.bind(MSG.setPasswordUnknownUser, userName));
                return;
            }
            
            final PersistenceManagerV2 persistence = this.getMyPolly().persistence();
            try {
                persistence.writeAtomic(new Atomic() {
                    
                    @Override
                    public void perform(Write write) {
                        u.setPassword(newPw);
                    }
                });
                this.reply(executer, MSG.setPasswordSuccess);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
    }
}
