package polly.eventhandler;

import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;


public class GhostHandler extends MessageAdapter {

    @Override
    public void noticeMessage(MessageEvent e) {
        if (e.getUser().getNickName().equalsIgnoreCase("nickserv")) {
            if (e.getMessage().toLowerCase().contains(
                        "ghost with your nick has been killed.")) {
                e.getSource().setAndIdentifyDefaultNickname();
            } else if (e.getMessage().toLowerCase().contains(
                        "password accepted - you are now recognized.")) {
                e.getSource().rejoinDefaultChannels();
            }
        }
    }
}
