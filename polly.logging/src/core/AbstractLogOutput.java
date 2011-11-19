package core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import de.skuzzle.polly.sdk.FormatManager;
import entities.LogEntry;


public abstract class AbstractLogOutput implements LogOutput {
   

    public String formatLogs(List<LogEntry> logs, LogFormatter formatter, 
                FormatManager pollyFormat) {
        
        PrintWriter pw = new PrintWriter(new StringWriter());
        for (LogEntry log : logs) {
            pw.println(formatter.formatLog(log, pollyFormat));
        }
        return pw.toString();
    }

}
