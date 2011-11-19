package core.output;

import java.util.Iterator;
import java.util.List;

import core.LogFormatter;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import entities.LogEntry;


public class IrcLogOutput implements LogOutput {

    
    @Override
    public void outputLogs(IrcManager irc, String channel, List<LogEntry> logs,
            LogFormatter formatter, FormatManager pollyFormat) {

        Iterator<LogEntry> it = logs.iterator();
        while (it.hasNext()) {
            LogEntry log = it.next();
            irc.sendMessage(channel, formatter.formatLog(log, pollyFormat), this);
        }
    }
    

}
