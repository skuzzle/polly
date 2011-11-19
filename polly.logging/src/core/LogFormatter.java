package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.LogEntry;


public interface LogFormatter {

    public String formatLog(LogEntry entry, FormatManager formatter);
}
