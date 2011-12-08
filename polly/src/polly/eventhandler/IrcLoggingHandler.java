package polly.eventhandler;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.IrcLogFilter;
import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;





public class IrcLoggingHandler implements MessageListener, NickChangeListener, 
        JoinPartListener {
    
	private static Logger logger = Logger.getLogger("IRCLOGGER");
    
	private List<IrcLogFilter> filters;
   
	public IrcLoggingHandler() {
		logger.trace("");
		logger.trace("----------------------------------------------");
		logger.trace("new IRC Session started.");
		logger.trace("----------------------------------------------");
		logger.trace("");
		
		this.filters = new LinkedList<IrcLogFilter>();
	}
	
	
	public void addFilter(IrcLogFilter filter) {
	    this.filters.add(filter);
	}
	
	
	public void removeFilter(IrcLogFilter filter) {
	    this.filters.add(filter);
	}
	
    
    @Override
    public void publicMessage(MessageEvent e) {
        if (!this.discardMessage(e)) {
            logger.trace(e);
        }
    }

    
    
    @Override
    public void privateMessage(MessageEvent e) {
        if (!this.discardMessage(e)) {
            logger.trace(e);
        }
    }
    
    

    @Override
    public void actionMessage(MessageEvent e) {
        if (!this.discardMessage(e)) {
            logger.trace(e);
        }
    }
    
    

    @Override
    public void nickChanged(NickChangeEvent e) {
        if (!this.discardNickChange(e)) {
            logger.trace(e);
        }
    }

    
    
    @Override
    public void channelJoined(ChannelEvent e) {
        if (!this.discardChannelEvent(e)) {
            logger.trace("*** JOIN " + e);
        }
    }

    
    
    @Override
    public void channelParted(ChannelEvent e) {
        if (!this.discardChannelEvent(e)) {
            logger.trace("*** PART " + e);
        }
    }
    
    
    private boolean discardMessage(MessageEvent e) {
        for (IrcLogFilter filter : this.filters) {
            if (filter.discard(e)) {
                return true;
            }
        }
        return false;
    }
    
    
    private boolean discardNickChange(NickChangeEvent e) {
        for (IrcLogFilter filter : this.filters) {
            if (filter.discard(e)) {
                return true;
            }
        }
        return false;
    }
    
    
    private boolean discardChannelEvent(ChannelEvent e) {
        for (IrcLogFilter filter : this.filters) {
            if (filter.discard(e)) {
                return true;
            }
        }
        return false;
    }

}
