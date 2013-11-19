package commands;


import java.util.List;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.Types.ListType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.time.Time;
import entities.RemindEntity;



public class LeaveCommand extends AbstractRemindCommand {


    public LeaveCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "leave"); //$NON-NLS-1$
        this.createSignature(MSG.leaveSig0Desc, 
                MyPlugin.LEAVE_PERMISSION,
                new Parameter(MSG.leaveSig0User, Types.USER), 
                new Parameter(MSG.leaveSig0Channel, Types.CHANNEL), 
                new Parameter(MSG.leaveSig0Message, Types.STRING));
        this.createSignature(MSG.leaveSig1Desc,
                MyPlugin.LEAVE_PERMISSION,
                new Parameter(MSG.leaveSig1Users, new ListType(Types.USER)), 
                new Parameter(MSG.leaveSig1Channel, Types.CHANNEL), 
                new Parameter(MSG.leaveSig1Message, Types.STRING));
        this.createSignature(MSG.leaveSig2Desc,
                MyPlugin.LEAVE_PERMISSION,
                new Parameter(MSG.leaveSig2User, Types.USER), 
                new Parameter(MSG.leaveSig2Message, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.leaveHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
             
        if (this.match(signature, 0)) {
            String location = signature.getStringValue(1);
            String msg = signature.getStringValue(2);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);

            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, location, 
                    Time.currentTime(), Time.currentTime());
            remind.setIsMessage(true);
            this.addRemind(executer, remind, false);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
            
        } else if (this.match(signature, 1)) {
            String location = signature.getStringValue(1);
            String msg = signature.getStringValue(2);
            String fromUser = executer.getCurrentNickName();
            
            List<UserType> users = signature.getListValue(UserType.class, 0);
            for (UserType ut : users) {
                RemindEntity remind = new RemindEntity(msg, fromUser, ut.getValue(), 
                        location, Time.currentTime(), Time.currentTime());
                remind.setIsMessage(true);
                this.addRemind(executer, remind, false);
            }
            ListType tmp = (ListType) signature.getValue(0);
            this.reply(channel,  MSG.bind(MSG.leaveMultipleSuccess, 
                    tmp.valueString(this.getMyPolly().formatting()))); 
            
        } else if (this.match(signature, 2)) {
            String msg = signature.getStringValue(1);
            String fromUser = executer.getCurrentNickName();
            String forUser = signature.getStringValue(0);
            
            RemindEntity remind = new RemindEntity(msg, fromUser, forUser, forUser, 
                    Time.currentTime(), Time.currentTime());
            remind.setIsMessage(true);
            this.addRemind(executer, remind, false);
            this.reply(channel, FORMATTER.format(remind, this.getMyPolly().formatting()));
            
        }
        return false; 
    }

}