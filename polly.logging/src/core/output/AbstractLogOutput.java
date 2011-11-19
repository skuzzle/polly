package core.output;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import core.LogFormatter;

import de.skuzzle.polly.sdk.FormatManager;
import entities.LogEntry;


public abstract class AbstractLogOutput implements LogOutput {
   

    public String formatLogs(List<LogEntry> logs, LogFormatter formatter, 
                FormatManager pollyFormat) {
        
        StringWriter string = new StringWriter();
        PrintWriter pw = new PrintWriter(string);
        
        Iterator<LogEntry> it = logs.iterator();
        while (it.hasNext()) {
            LogEntry log = it.next();
            pw.print(formatter.formatLog(log, pollyFormat));
            if (it.hasNext()) {
                pw.println();
            }
        }
        return string.toString();
    }

}
