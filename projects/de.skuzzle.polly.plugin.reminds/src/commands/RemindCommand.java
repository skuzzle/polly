package commands;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.Types.TimespanType;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownAttributeException;
import de.skuzzle.polly.sdk.time.Time;
import entities.RemindEntity;



public class RemindCommand extends AbstractRemindCommand {
	
	protected Logger logger = Logger.getLogger(RemindCommand.class.getName());

	public RemindCommand(MyPolly myPolly, RemindManager remindManager) 
            throws DuplicatedSignatureException {
        super(myPolly, remindManager, "remind"); //$NON-NLS-1$
        this.createSignature(MSG.remindCmdSig0Desc, 
            MyPlugin.REMIND_PERMISSION,
            new Parameter(MSG.remindCmdSig0User, Types.USER), 
            new Parameter(MSG.remindCmdSig0Channel, Types.CHANNEL), 
            new Parameter(MSG.remindCmdSig0Time, Types.DATE), 
            new Parameter(MSG.remindCmdSig0Message, Types.STRING));
        this.createSignature(MSG.remindCmdSig1Desc,
            MyPlugin.REMIND_PERMISSION,
            new Parameter(MSG.remindCmdSig1Users, new ListType(Types.USER)), 
            new Parameter(MSG.remindCmdSig1Channel, Types.CHANNEL), 
            new Parameter(MSG.remindCmdSig1Time, Types.DATE), 
            new Parameter(MSG.remindCmdSig1Message, Types.STRING));
        this.createSignature(MSG.remindCmdSig2Desc,
            MyPlugin.REMIND_PERMISSION,
            new Parameter(MSG.remindCmdSig2Time, Types.DATE), 
            new Parameter(MSG.remindCmdSig2Message, Types.STRING));
        this.createSignature(MSG.remindCmdSig3Desc,
            MyPlugin.REMIND_PERMISSION,
            new Parameter(MSG.remindCmdSig3User, Types.USER), 
            new Parameter(MSG.remindCmdSig3Time, Types.DATE), 
            new Parameter(MSG.remindCmdSig3Message, Types.STRING));
        this.createSignature(MSG.remindCmdSig4Desc,
    		MyPlugin.REMIND_PERMISSION,
    		new Parameter(MSG.remindCmdSig4Time, Types.DATE));
        this.createSignature(MSG.remindCmdSig5Desc);
        this.setRegisteredOnly();
        this.setHelpText(MSG.remindCmdHelp);
    }
	
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
               
        if (this.match(signature, 0)) {
            String location = signature.getStringValue(1);
            Date dueDate = signature.getDateValue(2);
            String msg = signature.getStringValue(3);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, location, 
                    dueDate, Time.currentTime());
            this.addRemind(executer, remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
            
        } else if (this.match(signature, 1)) {
            String location = signature.getStringValue(1);
            Date dueDate = signature.getDateValue(2);
            String msg = signature.getStringValue(3);
            String fromUser = executer.getCurrentNickName();
            
            List<UserType> users = signature.getListValue(UserType.class, 0);
            for (UserType ut : users) {
                RemindEntity remind = new RemindEntity(msg, fromUser, ut.getValue(), 
                        location, dueDate, Time.currentTime());
                this.addRemind(executer, remind, true);
            }
            ListType tmp = (ListType) signature.getValue(0);
            this.reply(channel, MSG.bind(MSG.remindCmdMultipleUsersSuccess, 
                    tmp.valueString(this.getMyPolly().formatting())));
            
        } else if (this.match(signature, 2)) {
            Date dueDate = signature.getDateValue(0);
            String msg = signature.getStringValue(1);
            
            RemindEntity remind = new RemindEntity(msg, executer.getCurrentNickName(), 
                    executer.getCurrentNickName(), channel, dueDate, Time.currentTime());
            this.addRemind(executer, remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        } else if (this.match(signature, 3)) {
            /*
             * ISSUE: 0000021
             * This signatures allows to create reminds that are delivered via query
             */
            String forUser = signature.getStringValue(0);
            Date dueDate = signature.getDateValue(1);
            String msg = signature.getStringValue(2);
            String fromUser = executer.getCurrentNickName();
            

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, forUser, 
                    dueDate, Time.currentTime());
            this.addRemind(executer, remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        } else  if (this.match(signature, 4)){
        	Date dueDate = signature.getDateValue(0);
        	final String msg;
        	try {
                msg = ((StringType) 
                    executer.getAttribute(MyPlugin.DEFAULT_MSG)).getValue();
                
            } catch (UnknownAttributeException e) {
                throw new CommandException(e);
            }
            
            RemindEntity remind = new RemindEntity(msg, executer.getCurrentNickName(), 
                    executer.getCurrentNickName(), channel, dueDate, Time.currentTime());
            this.addRemind(executer, remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        } else if (this.match(signature, 5)) {
            final String msg;
            long millis = Time.currentTimeMillis();
            try {
                msg = ((StringType) 
                    executer.getAttribute(MyPlugin.DEFAULT_MSG)).getValue();
                
                final TimespanType tst = (TimespanType) executer.getAttribute(
                    MyPlugin.DEFAULT_REMIND_TIME); 
                
                millis += tst.getSpan() * 1000;
            } catch (UnknownAttributeException e) {
                throw new CommandException(e);
            }
            Date dueDate = new Date(millis);
            RemindEntity remind = new RemindEntity(msg, executer.getCurrentNickName(), 
                    executer.getCurrentNickName(), channel, dueDate, Time.currentTime());
            this.addRemind(executer, remind, true);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
        }
        return false;
    }
}
