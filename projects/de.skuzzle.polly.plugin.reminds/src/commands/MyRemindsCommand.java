package commands;


import java.util.Collections;
import java.util.List;

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
            String mail = remind.isMail() ? ", E-Mail" : "";
            return "(" + remind.getId() + mail + ") Erinnerung für " + 
                    remind.getForUser() + 
                    " an: '" + remind.getMessage() + 
                    "', Zeit: " + 
                    formatter.formatDate(remind.getDueDate()) + ", hinterlassen am " + 
                    formatter.formatDate(remind.getLeaveDate()) + 
                    " von " + remind.getFromUser();
        }
        @Override
        protected String formatMessage(RemindEntity remind, FormatManager formatter) {
            return "(" + remind.getId() + ") Nachricht für " + remind.getForUser() + 
                    ": '" + remind.getMessage() + 
                    "', hinterlassen am " +
                    formatter.formatDate(remind.getLeaveDate()) + 
                    " von " + remind.getFromUser();
        }
    };
    
    
    
    
    public MyRemindsCommand(MyPolly polly, RemindManager manager) 
            throws DuplicatedSignatureException {
        super(polly, manager, "myreminds");
        this.createSignature("Zeigt die Reminds an, die dir oder die du für andere " +
        		"hinterlassen hast.",
        		MyPlugin.MY_REMINDS_PERMISSION);
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            List<RemindEntity> reminds = 
                this.remindManager.getDatabaseWrapper().getMyRemindsForUser(
                    executer.getCurrentNickName());
            
            if (reminds.isEmpty()) {
                this.reply(executer, "Keine Erinnerungen für dich vorhanden.");
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
