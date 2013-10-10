package de.skuzzle.polly.core.eventhandler;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.eventlistener.MessageAdapter;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;


public class GhostHandler extends MessageAdapter {

    private final static Logger logger = Logger.getLogger(GhostHandler.class
        .getName());
    
    @Override
    public void noticeMessage(MessageEvent e) {
        if (e.getUser().getNickName().equalsIgnoreCase("nickserv")) { //$NON-NLS-1$
            if (e.getMessage().toLowerCase().contains(
                        "ghost with your nick has been killed.")) { //$NON-NLS-1$
                logger.info("Ghost detected - changing to default nickname"); //$NON-NLS-1$
                e.getSource().setAndIdentifyDefaultNickname();
            } else if (e.getMessage().toLowerCase().contains(
                        "password accepted - you are now recognized.")) { //$NON-NLS-1$
                logger.info(
                    "Nickserv authentification complete. Joining default channels."); //$NON-NLS-1$
                e.getSource().rejoinDefaultChannels();
            }
        }
    }
}
