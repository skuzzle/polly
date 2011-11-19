package commands;

import core.PollyLoggingManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;



public abstract class AbstractLogCommand extends Command {

    protected PollyLoggingManager logManager;
    
    
    public AbstractLogCommand(MyPolly polly, String commandName, 
                PollyLoggingManager logManager) {
        super(polly, commandName);
        this.logManager = logManager;
    }


}
