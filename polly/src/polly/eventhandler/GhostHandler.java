package polly.eventhandler;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;


public class GhostHandler extends MessageAdapter {

    private final static Logger logger = Logger.getLogger(GhostHandler.class
        .getName());
    
    @Override
    public void noticeMessage(MessageEvent e) {
        if (e.getUser().getNickName().equalsIgnoreCase("nickserv")) {
            if (e.getMessage().toLowerCase().contains(
                        "ghost with your nick has been killed.")) {
                logger.info("Ghost detected - changing to default nickname");
                e.getSource().setAndIdentifyDefaultNickname();
            } else if (e.getMessage().toLowerCase().contains(
                        "password accepted - you are now recognized.")) {
                logger.info(
                    "Nickserv authentification complete. Joining default channels.");
                e.getSource().rejoinDefaultChannels();
            }
        }
    }
}
