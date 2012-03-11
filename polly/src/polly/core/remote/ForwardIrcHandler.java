package polly.core.remote;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.MessageSendListener;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.OwnMessageEvent;
import de.skuzzle.polly.sdk.eventlistener.QuitEvent;
import de.skuzzle.polly.sdk.eventlistener.QuitListener;


public class ForwardIrcHandler implements MessageListener, NickChangeListener, 
        QuitListener, JoinPartListener, MessageSendListener {

    
    public ForwardIrcHandler(IrcManager ircManager) {
        ircManager.addJoinPartListener(this);
        ircManager.addMessageListener(this);
        ircManager.addMessageSendListener(this);
        ircManager.addNickChangeListener(this);
        ircManager.addQuitListener(this);
    }
    
    
    
    
    @Override
    public void messageSent(OwnMessageEvent e) {
    }

    @Override
    public void channelJoined(ChannelEvent e) {
    }

    @Override
    public void channelParted(ChannelEvent e) {
    }

    @Override
    public void quited(QuitEvent e) {
    }

    @Override
    public void nickChanged(NickChangeEvent e) {
    }

    @Override
    public void publicMessage(MessageEvent e) {
    }

    @Override
    public void privateMessage(MessageEvent e) {
    }

    @Override
    public void actionMessage(MessageEvent e) {
    }




    @Override
    public void noticeMessage(MessageEvent e) {
    }
    
}
