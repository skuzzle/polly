package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.core.orion.OrionChatProvider;
import polly.rx.core.orion.model.DefaultOrionChatEntry;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.time.Time;


public class IGMCommand extends Command {

    private final OrionChatProvider chatProvider;
    
    public IGMCommand(MyPolly polly, OrionChatProvider chatProvider) throws DuplicatedSignatureException {
        super(polly, "igm"); //$NON-NLS-1$
        this.chatProvider = chatProvider;
        this.createSignature(MSG.igmSig0Desc, RoleManager.REGISTERED_PERMISSION, 
                new Parameter(MSG.igmSig0ParamMsg, Types.STRING));
        this.setHelpText(MSG.igmHelp);
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final String msg = signature.getStringValue(0) + " " + //$NON-NLS-1$
                    MSG.bind(MSG.igmViaIrcPostfix, channel);
            final DefaultOrionChatEntry oce = new DefaultOrionChatEntry(
                    executer.getCurrentNickName(), msg, Time.currentTime());
            try {
                this.chatProvider.addChatEntry(oce);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }

}
