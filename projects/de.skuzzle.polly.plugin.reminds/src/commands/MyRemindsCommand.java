package commands;


import java.util.Collections;
import java.util.List;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import core.RemindFormatter;
import core.RemindManager;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import entities.RemindEntity;

public class MyRemindsCommand extends AbstractRemindCommand {
    
    protected final static RemindFormatter FORMATTER = new RemindFormatter() {
        
        @Override
        protected String formatRemind(RemindEntity remind, FormatManager formatter) {
            final String msg;
            if (remind.isMail()) {
                msg = MSG.myRemindFormatRemindMail;
            } else {
                msg = MSG.myRemindFormatRemind;
            }
            return MSG.bind(msg, remind.getId(), 
                    remind.getForUser(), 
                    remind.getMessage(), 
                    formatter.formatDate(remind.getDueDate()), 
                    formatter.formatDate(remind.getLeaveDate()), 
                    remind.getFromUser());
        }
        @Override
        protected String formatMessage(RemindEntity remind, FormatManager formatter) {
            return MSG.bind(MSG.myRemindFormatMessage, remind.getId(), 
                    remind.getForUser(), remind.getMessage(), 
                    formatter.formatDate(remind.getLeaveDate()), 
                    remind.getFromUser());
        }
    };
    
    
    
    
    public MyRemindsCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "myreminds"); //$NON-NLS-1$
        this.createSignature(MSG.myRemindSig0Desc, MyPlugin.MY_REMINDS_PERMISSION);
        this.setRegisteredOnly();
        this.setHelpText(MSG.myRemindHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            List<RemindEntity> reminds = 
                this.remindManager.getDatabaseWrapper().getMyRemindsForUser(
                    executer.getCurrentNickName());
            
            if (reminds.isEmpty()) {
                this.reply(executer, MSG.myRemindNoRemind);
            }
            Collections.sort(reminds);
            for (RemindEntity remind : reminds) {
                this.reply(executer, FORMATTER.format(remind, 
                        this.getMyPolly().formatting()));
            }
        }
        
        return false;
    }

}
