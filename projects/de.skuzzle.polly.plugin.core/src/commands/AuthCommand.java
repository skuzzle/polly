package commands;

import polly.core.Messages;
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
        super(polly, "auth");
        this.createSignature(Messages.authSig0Desc, 
            new Parameter(Messages.userName, Types.USER),
            new Parameter(Messages.authSigPassword, Types.STRING));
        this.createSignature(Messages.authSig1Desc, 
            new Parameter(Messages.authSigPassword, Types.STRING));
        this.setHelpText(Messages.authHelp);
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
        
        this.reply(channel, Messages.authQryWarning);
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        String userName = "";
        String password = "";
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
                this.reply(executer, Messages.authWrongPw);
                return;
            }
            this.reply(executer, Messages.authSuccess);
        } catch (UnknownUserException e) {
            this.reply(executer, Messages.bind(Messages.unknownUser, userName));
        } catch (AlreadySignedOnException e) {
            this.reply(executer, Messages.bind(Messages.authAlreadySignedOn, 
                    e.getUser().getCurrentNickName()));
        }
    }
}
    