package core;

import de.skuzzle.polly.sdk.FormatManager;
import entities.LogEntry;


public class DefaultLogFormatter implements LogFormatter {

    @Override
    public String formatLog(LogEntry entry, FormatManager formatter) {
        StringBuilder b = new StringBuilder();
               
        if (entry.getType() == LogEntry.TYPE_UNKNOWN) {
            b.append("Keine Daten für Benutzer " + entry.getNickname());
            return b.toString();
        }
        b.append(formatter.formatDate(entry.getDate()));
        b.append(" ");
        
        if (entry.getType() == LogEntry.TYPE_NICKCHANGE) {
            b.append(entry.getMessage());
        } else if (entry.getType() == LogEntry.TYPE_JOIN) {
            b.append(entry.getMessage());
        } else if(entry.getType() == LogEntry.TYPE_PART) {
            b.append(entry.getMessage());
        } else if (entry.getType() == LogEntry.TYPE_QUIT) {
            b.append(entry.getMessage());
        } else if (entry.getType() == LogEntry.TYPE_MESSAGE) {
            b.append(entry.getChannel());
            b.append(" <");
            b.append(entry.getNickname());
            b.append("> ");
            b.append(entry.getMessage());
        }
        
        return b.toString();
    }

}
