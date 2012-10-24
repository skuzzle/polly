package polly.core;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.time.Time;


public class NewYearCountdown {

    private final static int COUNTDOWN = 10; // 10 seconds
    
    
    public NewYearCountdown(Date destination, final IrcManager ircManager) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        
        long dest = destination.getTime();
        long now = Time.currentTimeMillis();
        
        long countdown = Milliseconds.fromSeconds(COUNTDOWN);
        long delay = dest - now - countdown;
        
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < COUNTDOWN; ++i) {
                    try {
                        Thread.sleep(Milliseconds.fromSeconds(1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    for (final String channel : ircManager.getChannels()) {
                        ircManager.sendMessage(channel, "" + (COUNTDOWN - i));
                    }
                }
                
                for (final String channel : ircManager.getChannels()) {
                    ircManager.sendMessage(channel, "Frohes Neues Jahr");
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
}
