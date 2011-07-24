package polly.eventhandler;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;





public class IrcLoggingHandler implements 
    MessageListener, NickChangeListener, JoinPartListener {
	private static Logger logger = Logger.getLogger("IRCLOGGER");
    
   
	public IrcLoggingHandler() {
		logger.trace("");
		logger.trace("----------------------------------------------");
		logger.trace("new IRC Session started.");
		logger.trace("----------------------------------------------");
		logger.trace("");
	}
    
    @Override
    public void publicMessage(MessageEvent e) {
    	logger.trace(e);
    }

    
    
    @Override
    public void privateMessage(MessageEvent e) {
    	logger.trace(e);
    }
    
    

    @Override
    public void actionMessage(MessageEvent e) {
    	logger.trace(e);
    }
    
    

    @Override
    public void nickChanged(NickChangeEvent e) {
    	logger.trace(e);
    }

    
    
    @Override
    public void channelJoined(ChannelEvent e) {
    	logger.trace("*** JOIN " + e);
    }

    
    
    @Override
    public void channelParted(ChannelEvent e) {
    	logger.trace("*** PART " + e);
    }

}
