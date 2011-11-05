package core;

import java.util.List;

import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import entities.RemindEntity;

public class DeliverRemindHandler extends JoinPartAdapter implements MessageListener {

    private RemindManager remindManager;
    
    public DeliverRemindHandler(RemindManager remindManager) {
        this.remindManager = remindManager;
    }
    
    
    
    @Override
    public void channelJoined(ChannelEvent e) {
        this.deliverRemind(e, true);
    }
    
    
    @Override
    public void publicMessage(MessageEvent e) {
        this.deliverRemind(e, false);
    }



    @Override
    public void privateMessage(MessageEvent e) {
        this.deliverRemind(e, false);
    }



    @Override
    public void actionMessage(MessageEvent e) {
        this.deliverRemind(e, false);
    }

    
    private void deliverRemind(ChannelEvent e, boolean join) {
        List<RemindEntity> reminds = this.remindManager.undeliveredReminds(e.getUser());
        for (RemindEntity remind : reminds) {
            
            /*
             * Two opportunities to deliver leave messages:
             * 1. the message was left in a channel #c and the destined user joins this 
             *    channel
             * 2. the message was left to be delivered in query. Then it is delivered
             *    when the user joins any channel.
             */
            boolean deliverLeave = join && 
                    (!remind.getOnChannel().startsWith("#") || 
                      remind.getOnChannel().equals(e.getChannel()));
            
            if (remind.isOnAction() || deliverLeave) {
                /*
                 * second condition is to check messages that are to deliver in query 
                 */
                
                // remind.setNotDelivered(false);   // enable if leave messages should 
                                                    // not be 'sleepable'
                this.remindManager.deliverRemind(remind);
            }
        }
    }


}
