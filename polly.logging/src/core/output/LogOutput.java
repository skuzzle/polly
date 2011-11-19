package core.output;

import java.util.List;

import core.LogFormatter;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import entities.LogEntry;


public interface LogOutput {

    public abstract void outputLogs(IrcManager irc, String channel, 
            List<LogEntry> logs, LogFormatter formatter, FormatManager pollyFormat);
}