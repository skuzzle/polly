package polly.rx;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;


public class DailyGreeter {

    
    private final static Set<String> GREETINGS;
    static {
        final String[] greetings = {
            "Hallo", "gumo", "Morgen", "Nabend", "Moin", "Hi", "Servus",
            "Sers", "Jo", "Hey", "Aloha", "puh", "Moin moin", "wb", "Guten Abend"
        };
        GREETINGS = new HashSet<String>(Arrays.asList(greetings));
    }
    
    
    
    private final Set<IrcUser> greeted;
    
    private final UserSpottedListener spottedListener = new UserSpottedListener() {
        @Override
        public void userSpotted(SpotEvent ignore) {}
        
        
        @Override
        public void userLost(SpotEvent e) {
            greeted.remove(e.getUser());
        }
    };
    
    
    
    private final MessageListener messageListener = new MessageAdapter() {
        @Override
        public void publicMessage(MessageEvent e) {
            this.deliverGreet(e);
        }
        
        public void privateMessage(MessageEvent e) {
            this.deliverGreet(e);
        };
        
        
        
        private final void deliverGreet(MessageEvent e) {
            if (greeted.contains(e.getUser())) {
                return;
            }
            final String msg = e.getMessage().toLowerCase();
            final String nick = e.getSource().getNickname().toLowerCase();
            if (!msg.contains(nick)) {
                return;
            }
            
            for (String greeting : GREETINGS) {
                if (msg.contains(greeting.toLowerCase())) {
                    greeted.add(e.getUser());
                    String greet = greeting + " " + e.getUser().getNickName();
                    e.getSource().sendMessage(e.getChannel(), greet, this);
                    return;
                }
            }
        }
    };
    
    
    
    public DailyGreeter() {
        this.greeted = Collections.synchronizedSet(new HashSet<IrcUser>());
    }
    
    
    
    public void deploy(IrcManager ircManager) {
        ircManager.addUserSpottedListener(this.spottedListener);
        ircManager.addMessageListener(this.messageListener);
    }
    
    
    
    public void undeploy(IrcManager ircManager) {
        ircManager.removeUserSpottedListener(this.spottedListener);
        ircManager.removeMessageListener(this.messageListener);
    }
}
