package core;

import java.util.List;

import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.JoinPartAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.UserEvent;
import de.skuzzle.polly.sdk.eventlistener.UserListener;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.RemindEntity;

public class DeliverRemindHandler extends JoinPartAdapter implements MessageListener, 
        UserListener {

    private RemindManager remindManager;
    private UserManager userManager;

    
    
    public DeliverRemindHandler(RemindManager remindManager, UserManager userManager) {
        this.remindManager = remindManager;
        this.userManager = userManager;
    }
    
    
    
    @Override
    public void channelJoined(ChannelEvent e) {
        this.deliverRemind(e, true, false);
    }
    
    
    @Override
    public void publicMessage(MessageEvent e) {
        if (this.remindManager.onReturnAvailable(e.getUser().getNickName())) {
            this.deliverRemind(e, false, false);
        }
    }



    @Override
    public void privateMessage(MessageEvent e) {
        if (this.remindManager.onReturnAvailable(e.getUser().getNickName())) {
            this.deliverRemind(e, false, false);
        }
    }



    @Override
    public void actionMessage(MessageEvent e) {
        if (this.remindManager.onReturnAvailable(e.getUser().getNickName())) {
            this.deliverRemind(e, false, false);
        }
    }

    
    
    private synchronized void deliverRemind(ChannelEvent e, boolean join, boolean signOn) {
        if (join && signOn) {
            throw new IllegalArgumentException("cant be join and signon!");
        }
        
        List<RemindEntity> reminds = this.remindManager.undeliveredReminds(e.getUser());       
        for (RemindEntity remind : reminds) {

            /*
             * If this is a signon, there is no valid channel set in the ChannelEvent,
             * so we use the destination channel of the current remind.
             */
            String channel = signOn ? remind.getOnChannel() : e.getChannel();

            /*
             * Two opportunities to deliver leave messages:
             * 1. the message was left in a channel #c and the destined user joins this 
             *    channel
             * 2. the message was left to be delivered in query. Then it is delivered
             *    when the user joins any channel.
             */
            boolean deliverLeave = (join || signOn) && 
                    (!channel.startsWith("#") || 
                      channel.equals(remind.getOnChannel()));

            /*
             * The remind only gets delivered of the destination user is signed on or
             * if no user with the destination user name exists.
             */
            deliverLeave &= this.userManager.isSignedOn(e.getUser()) ||
                            this.userManager.getUser(remind.getForUser()) == null;

            
            if (remind.isOnAction() || deliverLeave) {                
                // remind.setNotDelivered(false);   // enable if leave messages should 
                                                    // not be 'sleepable'
                try {
                    this.remindManager.deliverRemind(remind);
                } catch (DatabaseException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }



    @Override
    public void userSignedOn(UserEvent e) {
        ChannelEvent c = new ChannelEvent(null, 
                new IrcUser(e.getUser().getCurrentNickName(), "", ""), "");
        this.deliverRemind(c, false, true);
    }



    @Override
    public void userSignedOff(UserEvent ignore) {}


}
