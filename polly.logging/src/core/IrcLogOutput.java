package core;

import java.util.List;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import entities.LogEntry;


public class IrcLogOutput extends AbstractLogOutput {

    
    @Override
    public void outputLogs(IrcManager irc, String channel, List<LogEntry> logs,
            LogFormatter formatter, FormatManager pollyFormat) {
        
        String logString = this.formatLogs(logs, formatter, pollyFormat);
        irc.sendMessage(channel, logString, this);
    }
    

}
