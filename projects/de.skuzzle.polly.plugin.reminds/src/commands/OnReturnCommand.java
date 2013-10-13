package commands;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.time.Time;
import entities.RemindEntity;


public class OnReturnCommand extends AbstractRemindCommand {

    public OnReturnCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "onreturn"); //$NON-NLS-1$
        this.createSignature(MSG.onReturnSig0Desc, 
    		MyPlugin.ON_RETURN_PERMISSION,
    		new Parameter(MSG.onReturnSig0User, Types.USER), 
    		new Parameter(MSG.onReturnSig0Message, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.onReturnHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String msg = signature.getStringValue(1);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, channel, 
                    Time.currentTime(), Time.currentTime());
            remind.setIsMessage(true);
            remind.setOnAction(true);
            this.addRemind(executer, remind, false);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        }
        
        return false;
    }

}
