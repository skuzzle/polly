package core;

import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.eventlistener.NickChangeEvent;
import de.skuzzle.polly.sdk.eventlistener.NickChangeListener;
import de.skuzzle.polly.sdk.eventlistener.SpotEvent;
import de.skuzzle.polly.sdk.eventlistener.UserSpottedListener;
import de.skuzzle.polly.sdk.time.TimeProvider;


public class JoinTimeCollector implements UserSpottedListener, NickChangeListener {

    private Map<String, Long> joinTimes;
    private TimeProvider timeProvider;
    
    
    
    public JoinTimeCollector(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        this.joinTimes = new HashMap<String, Long>();
    }
    
    
    
    public void addTo(IrcManager ircManager) {
        ircManager.addUserSpottedListener(this);
        ircManager.addNickChangeListener(this);
    }
    
    
    
    public void remove(IrcManager ircManager) {
        ircManager.removeUserSpottedListener(this);
        ircManager.removeNickChangeListener(this);
    }
    
    
    
    public Long getJoinTime(String nickName) {
        synchronized (this.joinTimes) {
            return this.joinTimes.get(nickName);
        }
    }
    
    
    
    @Override
    public void userSpotted(SpotEvent e) {
        synchronized (this.joinTimes) {
            this.joinTimes.put(e.getUser().getNickName(), 
                this.timeProvider.currentTimeMillis());
        }
    }
    
    

    @Override
    public void userLost(SpotEvent e) {
        synchronized (this.joinTimes) {
            this.joinTimes.remove(e.getUser().getNickName());
        }
    }



    @Override
    public void nickChanged(NickChangeEvent e) {
        synchronized (this.joinTimes) {
            Long joinTime = this.joinTimes.get(e.getOldUser().getNickName());
            if (joinTime != null) {
                this.joinTimes.remove(e.getOldUser().getNickName());
                this.joinTimes.put(e.getNewUser().getNickName(), joinTime);
            }
        }
    }
    
}