package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;



public class AuthCommand extends Command {
    
    public AuthCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "auth"); //$NON-NLS-1$
        this.createSignature(MSG.authSig0Desc.s, 
            new Parameter(MSG.userName.s, Types.USER),
            new Parameter(MSG.authSigPassword.s, Types.STRING));
        this.createSignature(MSG.authSig1Desc.s, 
            new Parameter(MSG.authSigPassword.s, Types.STRING));
        this.setHelpText(MSG.authHelp.s);
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
        
        this.reply(channel, MSG.authQryWarning.s);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        String userName = ""; //$NON-NLS-1$
        String password = ""; //$NON-NLS-1$
        if (this.match(signature, 0)) {
            userName = signature.getStringValue(0);
            password = signature.getStringValue(1);
        } else if (this.match(signature, 1)) {
            userName = executer.getCurrentNickName();
            password = signature.getStringValue(0);
        }
        User user = null;
        try {
            user = this.getMyPolly().users().logon(executer.getCurrentNickName(), 
                    userName, password);
            
            if (user == null) {
                this.reply(executer, MSG.authWrongPw.s);
                return;
            }
            this.reply(executer, MSG.authSuccess.s);
        } catch (UnknownUserException e) {
            this.reply(executer, MSG.unknownUser.s(userName));
        } catch (AlreadySignedOnException e) {
            this.reply(executer, MSG.authAlreadySignedOn.s(
                    e.getUser().getCurrentNickName()));
        }
    }
}
    