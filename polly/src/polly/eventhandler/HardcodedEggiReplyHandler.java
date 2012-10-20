package polly.eventhandler;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;

public class HardcodedEggiReplyHandler implements MessageListener {

    @Override
    public void publicMessage(MessageEvent e) {
        if (e.getMessage().toLowerCase().contains("hallo")
            && e.getMessage().toLowerCase().contains(e.getSource().getNickname())) {
            
            e.getSource().sendMessage(e.getChannel(),
                "Hallo " + e.getUser().getNickName(), this);
        }
    }



    @Override
    public void privateMessage(MessageEvent ignore) {
    }



    @Override
    public void actionMessage(MessageEvent ignore) {
    }



    @Override
    public void noticeMessage(MessageEvent ignore) {
    }
}
