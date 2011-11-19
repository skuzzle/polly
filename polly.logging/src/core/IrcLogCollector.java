package core;

import java.util.Date;

import de.skuzzle.polly.sdk.eventlistener.ChannelEvent;
import de.skuzzle.polly.sdk.eventlistener.JoinPartListener;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.QuitEvent;
import de.skuzzle.polly.sdk.eventlistener.QuitListener;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import entities.LogEntry;

public class IrcLogCollector implements 
        MessageListener, JoinPartListener, QuitListener, NickChangeListener {

    private PollyLoggingManager logManager;
    
    
    public IrcLogCollector(PollyLoggingManager logManager) {
        this.logManager = logManager;
    }
    
    
    
    @Override
    public void nickChanged(NickChangeEvent e) {
        try {
            this.logManager.logMessage(LogEntry.forNickChange(
                    e.getOldUser().getNickName(), 
                    e.toString(), "", new Date()));
        } catch (DatabaseException e1) {
            this.onDatabaseException(e1);
        }
    }
    
    

    @Override
    public void quited(QuitEvent e) {
        try {
            this.logManager.logMessage(LogEntry.forQuit(
                        e.getUser().getNickName(), e.toString(), "", new Date()));
        } catch (DatabaseException e1) {
            this.onDatabaseException(e1);
        }
    }
    
    

    @Override
    public void channelJoined(ChannelEvent e) {
        try {
            this.logManager.logMessage(LogEntry.forJoin(
                        e.getUser().getNickName(),"*** JOIN " + e.toString(), 
                        e.getChannel(), new Date()));
        } catch (DatabaseException e1) {
            this.onDatabaseException(e1);
        }
    }

    
    
    @Override
    public void channelParted(ChannelEvent e) {
        try {
            this.logManager.logMessage(LogEntry.forPart(
                        e.getUser().getNickName(), "*** PART " + e.toString(), 
                        e.getChannel(), new Date()));
        } catch (DatabaseException e1) {
            this.onDatabaseException(e1);
        }
    }

    
    
    @Override
    public void publicMessage(MessageEvent e) {
        try {
            this.logManager.logMessage(LogEntry.forMessage(
                        e.getUser().getNickName(), 
                        e.getMessage(), e.getChannel(), new Date()));
        } catch (DatabaseException e1) {
            this.onDatabaseException(e1);
        }
    }

    
    
    @Override
    public void privateMessage(MessageEvent ignore) {
        // do not log messages in query 
    }

    
    
    @Override
    public void actionMessage(MessageEvent ignore) {
        // do not log action messages
    }
    
    
    
    private void onDatabaseException(DatabaseException e) {
        e.printStackTrace();
    }
    
}