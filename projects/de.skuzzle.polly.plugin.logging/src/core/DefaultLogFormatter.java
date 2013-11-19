package core;

import polly.logging.MSG;
import de.skuzzle.polly.sdk.FormatManager;
import entities.LogEntry;


public class DefaultLogFormatter implements LogFormatter {

    @Override
    public String formatLog(LogEntry entry, FormatManager formatter) {
        StringBuilder b = new StringBuilder();
               
        if (entry.getType() == LogEntry.TYPE_UNKNOWN) {
            b.append(MSG.bind(MSG.logFormatNoData, entry.getNickname()));
            return b.toString();
        }
        b.append(formatter.formatDate(entry.getDate()));
        b.append(" "); //$NON-NLS-1$
        
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
            b.append(" <"); //$NON-NLS-1$
            b.append(entry.getNickname());
            b.append("> "); //$NON-NLS-1$
            b.append(entry.getMessage());
        }
        
        return b.toString();
    }

}
