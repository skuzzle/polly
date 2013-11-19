package commands;

import polly.core.MSG;
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



public class SetMyPasswordCommand extends Command {

    public SetMyPasswordCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "setmypw"); //$NON-NLS-1$
        this.createSignature(MSG.setMyPwSig0Desc, 
        		new Parameter(MSG.setMyPwSig0CurrentPw, Types.STRING), 
        		new Parameter(MSG.setMyPwSig0NewPw, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.setMyPwHelp);
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
        this.reply(channel, MSG.setMyPwQryWarning);
    }
    
    
    
    @Override
    protected void executeOnQuery(final User executer, Signature signature) 
            throws CommandException {
        if (this.match(signature, 0)) {
            String oldPw = signature.getStringValue(0);
            final String newPw = signature.getStringValue(1);
            
            if (!executer.checkPassword(oldPw)) {
                this.reply(executer, MSG.setMyPwMismatch);
                return;
            }
            
            final PersistenceManagerV2 persistence = this.getMyPolly().persistence();
            try {
                persistence.writeAtomic(new Atomic() {
                    
                    @Override
                    public void perform(Write write) {
                        executer.setPassword(newPw);
                    }
                });
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
    }
}
