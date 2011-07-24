package core;

import java.util.List;

import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartAdapter;
import entities.RemindEntity;

public class DeliverRemindHandler extends JoinPartAdapter {

    private RemindManager remindManager;
    
    public DeliverRemindHandler(RemindManager remindManager) {
        this.remindManager = remindManager;
    }
    
    
    
    @Override
    public void channelJoined(ChannelEvent e) {
        List<RemindEntity> reminds = this.remindManager.undeliveredReminds(e.getUser());
        for (RemindEntity remind : reminds) {
            if (!remind.getOnChannel().startsWith("#") || 
                remind.getOnChannel().equals(e.getChannel())) {
                /*
                 * First condition is to check messages that are to deliver in query 
                 */
                
                // remind.setNotDelivered(false);   // enable if leave messages should 
                                                    // not be 'sleepable'
                this.remindManager.deliverRemind(remind);
            }
        }
    }
}
