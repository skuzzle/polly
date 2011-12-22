package core;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.eventlistener.UserEvent;
import de.skuzzle.polly.sdk.eventlistener.UserListener;

public class GreetDeliverer implements UserListener {

    private MyPolly myPolly;
    
    public GreetDeliverer(MyPolly myPolly) {
        this.myPolly = myPolly;
    }

    @Override
    public void userSignedOn(UserEvent e) {
        
        String greet = e.getUser().getAttribute(MyPlugin.GREETING);
        if (greet != null) {
            this.myPolly.irc().sendMessage(e.getUser().getCurrentNickName(), greet, this);
        }
    }

    
    
    @Override
    public void userSignedOff(UserEvent e) {}
}
