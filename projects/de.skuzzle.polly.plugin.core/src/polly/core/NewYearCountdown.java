package polly.core;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.time.Time;


public class NewYearCountdown {

    
    public NewYearCountdown(Date destination, final IrcManager ircManager) {
        
        final long dest = destination.getTime();
        final long now = Time.currentTimeMillis();
        final long delay = dest - now;
        
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                for (final String channel : ircManager.getChannels()) {
                    ircManager.sendMessage(channel, MSG.newYearHappyNewYear);
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
