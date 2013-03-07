package commands;

import core.PollyLoggingManager;
import de.skuzzle.polly.sdk.DelayedCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.time.Milliseconds;



public abstract class AbstractLogCommand extends DelayedCommand {

    private final static long LOG_DELAY = Milliseconds.fromSeconds(20);
    protected PollyLoggingManager logManager;
    
    
    public AbstractLogCommand(MyPolly polly, String commandName, 
                PollyLoggingManager logManager) {
        super(polly, commandName, (int) LOG_DELAY);
        this.logManager = logManager;
    }


}
