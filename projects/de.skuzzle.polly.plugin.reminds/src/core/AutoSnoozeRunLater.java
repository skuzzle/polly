package core;

import polly.reminds.MSG;
import polly.reminds.MyPlugin;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.tools.concurrent.RunLater;
import entities.RemindEntity;


public class AutoSnoozeRunLater extends RunLater {

    private final MessageListener listener;
    private final IrcManager ircManager;
    private final User forUser;
    
    
    public AutoSnoozeRunLater(String name, final User forUser, long timespan, 
            final IrcManager ircManager, final RemindManager remindManager, 
            final FormatManager formatter) {
        
        super(name, timespan);
        this.ircManager = ircManager;
        this.forUser = forUser;
        this.listener = new MessageAdapter() {
            @Override
            public void privateMessage(MessageEvent e) {
                final String nick = forUser.getCurrentNickName();
                final String indicator = ((StringType) forUser.getAttribute(
                        MyPlugin.AUTO_SNOOZE_INDICATOR)).getValue();
                
                if (e.getUser().getNickName().equals(nick)) {
                    AutoSnoozeRunLater.this.stop();
                    if (e.getMessage().equals(indicator)) {
                        try {
                            RemindEntity re = remindManager.snooze(forUser);
                            ircManager.sendMessage(nick, MSG.bind(MSG.snoozeSuccess, 
                                    formatter.formatDate(re.getDueDate()),
                                    re.getId()), this);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };
    }
    
    
    
    @Override
    public void started() {
        ircManager.addMessageListener(this.listener);
    }
    

    
    @Override
    public void run() {
    }
    
    
    
    @Override
    public void finished() {
        this.ircManager.sendMessage(this.forUser.getCurrentNickName(), 
            MSG.autoSnoozeOff, this);
        this.ircManager.removeMessageListener(this.listener);
    }
}
