package commands;


import java.util.List;

import core.RemindFormatter;
import core.RemindManagerImpl;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.RemindEntity;

public class MyRemindsCommand extends Command {
    
    protected final static RemindFormatter FORMATTER = new RemindFormatter() {
        
        @Override
        protected String formatRemind(RemindEntity remind, FormatManager formatter) {
            String mail = remind.isMail() ? ", E-Mail" : "";
            return "(" + remind.getId() + mail + ") Erinnerung f�r " + 
                    remind.getForUser() + 
                    " an: '" + remind.getMessage() + 
                    "', Zeit: " + 
                    formatter.formatDate(remind.getDueDate()) + ", hinterlassen am " + 
                    formatter.formatDate(remind.getLeaveDate()) + 
                    " von " + remind.getFromUser();
        }
        @Override
        protected String formatMessage(RemindEntity remind, FormatManager formatter) {
            return "(" + remind.getId() + ") Nachricht f�r " + remind.getForUser() + 
                    ": '" + remind.getMessage() + 
                    "', hinterlassen am " +
                    formatter.formatDate(remind.getLeaveDate()) + 
                    " von " + remind.getFromUser();
        }
    };
    
    
    
    private RemindManagerImpl remindManagerImpl;
    
    public MyRemindsCommand(MyPolly polly, RemindManagerImpl manager) 
            throws DuplicatedSignatureException {
        super(polly, "myreminds");
        this.createSignature("Zeigt die Reminds an, die dir oder die du f�r andere " +
        		"hinterlassen hast.");
        this.remindManagerImpl = manager;
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            List<RemindEntity> reminds = 
                this.remindManagerImpl.getMyReminds(executer.getCurrentNickName());
            
            if (reminds.isEmpty()) {
                this.reply(executer, "Keine Erinnerungen f�r dich vorhanden.");
            }
            for (RemindEntity remind : reminds) {
                this.reply(executer, FORMATTER.format(remind, 
                        this.getMyPolly().formatting()));
            }
        }
        
        return false;
    }

}
