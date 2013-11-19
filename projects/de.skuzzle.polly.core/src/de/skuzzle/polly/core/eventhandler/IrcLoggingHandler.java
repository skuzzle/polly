package de.skuzzle.polly.core.eventhandler;

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
    
	private static Logger logger = Logger.getLogger("IRCLOGGER"); //$NON-NLS-1$
    
	private List<IrcLogFilter> filters;
   
	public IrcLoggingHandler() {
		logger.trace(""); //$NON-NLS-1$
		logger.trace("----------------------------------------------"); //$NON-NLS-1$
		logger.trace("new IRC Session started."); //$NON-NLS-1$
		logger.trace("----------------------------------------------"); //$NON-NLS-1$
		logger.trace(""); //$NON-NLS-1$
		
		this.filters = new LinkedList<IrcLogFilter>();
	}
	
	
	public void addFilter(IrcLogFilter filter) {
	    this.filters.add(filter);
	}
	
	
	public void removeFilter(IrcLogFilter filter) {
	    this.filters.add(filter);
	}
	
	
	
    @Override
    public void noticeMessage(MessageEvent e) {
        if (!this.discardMessage(e)) {
            logger.trace(e);
        }
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
            logger.trace("*** JOIN " + e); //$NON-NLS-1$
        }
    }

    
    
    @Override
    public void channelParted(ChannelEvent e) {
        if (!this.discardChannelEvent(e)) {
            logger.trace("*** PART " + e); //$NON-NLS-1$
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
