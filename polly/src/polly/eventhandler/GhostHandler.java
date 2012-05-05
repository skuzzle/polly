package polly.eventhandler;

import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;


public class GhostHandler extends MessageAdapter {

    @Override
    public void noticeMessage(MessageEvent e) {
        if (e.getUser().getNickName().equalsIgnoreCase("nickserv")) {
            if (e.getMessage().toLowerCase().indexOf(
                        "ghost with your nick has been killed.") != -1) {
                e.getSource().setAndIdentifyDefaultNickname();
            }
        }
    }
}
