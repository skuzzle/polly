package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;


public class GhostCommand extends Command {

    public GhostCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "ghost"); //$NON-NLS-1$
        this.createSignature(MSG.ghostSig0Desc.s, 
            new Parameter(MSG.ghostSig0User.s, Types.USER), 
            new Parameter(MSG.ghostSig0Password.s, Types.STRING));
        this.setHelpText(MSG.ghostHelp.s);
        this.setQryCommand(true);
        
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
        Signature signature) throws CommandException {
        this.reply(channel, MSG.ghostQryOnly.s);
    }
    
    
    protected void executeOnQuery(User executer, Signature signature) 
            throws CommandException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String pw = signature.getStringValue(1);
            
            User user = this.getMyPolly().users().getUser(userName);
            
            if (user == null) {
                this.reply(executer, MSG.ghostNotLoggedIn.s(userName));
            } else if (!user.checkPassword(pw)) {
                this.reply(executer, MSG.wrongPassword.s);
            } else {
                try {
                    this.getMyPolly().users().logoff(user);
                    this.reply(executer, MSG.ghostLoggedOut.s(user.getName()));
                } catch (UnknownUserException e) {
                    throw new CommandException(e);
                }
            }
        }
    };
}