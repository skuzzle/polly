package core;

import org.jibble.jmegahal.JMegaHal;

import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.time.TimeProvider;


public class HighlightReplyHandler implements MessageListener {
    
    
    
    public static enum Mode {
        OFF, COLLECTING, REPLYING, BOTH;
    }
    
    
    
    private final static long MESSAGE_DELAY = 60000 * 5;
    
    
    private JMegaHal hal = new JMegaHal();
    private long lastMessage;
    private Mode mode;
    private TimeProvider timeProvider;
    
    
    public HighlightReplyHandler(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        this.lastMessage = timeProvider.currentTimeMillis() - MESSAGE_DELAY;
        this.mode = Mode.BOTH;
    }
    
    
    
    public Mode getMode() {
        return this.mode;
    }
    
    
    
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    

    
    public void resetTimeOut() {
        this.lastMessage = System.currentTimeMillis() - MESSAGE_DELAY;
    }
    
    
    
    @Override
    public void publicMessage(MessageEvent e) {
        if (this.mode == Mode.COLLECTING || this.mode == Mode.BOTH) {
            this.hal.add(e.getMessage());
        }
        this.onMessage(e);
    }
    
    
    
    @Override
    public void actionMessage(MessageEvent e) {
        this.onMessage(e);
    }

    
    
    private void onMessage(MessageEvent e) {
        if (this.mode == Mode.OFF || this.mode == Mode.COLLECTING || 
                this.timeProvider.currentTimeMillis() - this.lastMessage < MESSAGE_DELAY) {
            return;
        }
        String nick = e.getSource().getNickname().toLowerCase();
        String msg = e.getMessage().toLowerCase();
        if (msg.indexOf(nick) != -1) {
            //String sentence = this.builder.createSentence(10);
            String sentence = this.hal.getSentence(e.getUser().getNickName());
            e.getSource().sendMessage(e.getChannel(), sentence);
            this.lastMessage = System.currentTimeMillis();
        }
    }
    
    
    
    @Override
    public void privateMessage(MessageEvent ignore) {}



    @Override
    public void noticeMessage(MessageEvent ignore) {}
}
