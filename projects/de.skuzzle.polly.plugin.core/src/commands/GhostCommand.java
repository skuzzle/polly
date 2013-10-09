package commands;

import polly.core.Messages;
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
        super(polly, "ghost");
        this.createSignature(Messages.ghostSig0Desc, 
            new Parameter(Messages.ghostSig0User, Types.USER), 
            new Parameter(Messages.ghostSig0Password, Types.STRING));
        this.setHelpText(Messages.ghostHelp);
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
        this.reply(channel, Messages.ghostQryOnly);
    }
    
    
    protected void executeOnQuery(User executer, Signature signature) 
            throws CommandException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String pw = signature.getStringValue(1);
            
            User user = this.getMyPolly().users().getUser(userName);
            
            if (user == null) {
                this.reply(executer, Messages.bind(Messages.ghostNotLoggedIn, userName));
            } else if (!user.checkPassword(pw)) {
                this.reply(executer, Messages.wrongPassword);
            } else {
                try {
                    this.getMyPolly().users().logoff(user);
                    this.reply(executer, Messages.bind(Messages.ghostLoggedOut, 
                            user.getName()));
                } catch (UnknownUserException e) {
                    throw new CommandException(e);
                }
            }
            
        }
    };
}