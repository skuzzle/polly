package polly.eventhandler;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;


public class HardcodedEggiReplyHandler implements MessageListener {

    @Override
    public void publicMessage(MessageEvent e) {
        if (e.getChannel().equals("#regenbogen") && 
            e.getUser().getNickName().equals("Eggi") && 
            e.getMessage().toLowerCase().contains("hallo") && 
            e.getMessage().toLowerCase().contains("polly")) {
            
            e.getSource().sendMessage("#regenbogen", "Hallo Eggi");
        }
    }

    @Override
    public void privateMessage(MessageEvent ignore) {}

    @Override
    public void actionMessage(MessageEvent ignore) {}

    @Override
    public void noticeMessage(MessageEvent ignore) {}
}
