package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.LogEntry;


public class DefaultLogFormatter implements LogFormatter {

    @Override
    public String formatLog(LogEntry entry, FormatManager formatter) {
        StringBuilder b = new StringBuilder();
        
        b.append("[");
        b.append(formatter.formatDate(entry.getDate()));
        b.append("] ");
        if (entry.getType() != LogEntry.TYPE_QUIT && entry.getType() != LogEntry.TYPE_NICKCHANGE) {
            b.append(entry.getChannel());
        }
        b.append(" <");
        b.append(entry.getUser());
        b.append("> ");
        b.append(entry.getMessage());
        
        return b.toString();
    }

}
