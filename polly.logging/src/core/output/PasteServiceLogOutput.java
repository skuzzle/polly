package core.output;

import java.util.List;

import core.LogFormatter;

import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.IrcManager;
import de.skuzzle.polly.sdk.paste.PasteService;
import entities.LogEntry;


public class PasteServiceLogOutput extends AbstractLogOutput {
    
    private PasteService paster;
    
    
    public PasteServiceLogOutput(PasteService paster) {
        this.paster = paster;
    }
    

    
    @Override
    public void outputLogs(IrcManager irc, String channel, 
            List<LogEntry> logs, int unfilteredSize,
            LogFormatter formatter, FormatManager pollyFormat) {
        
        String logString = this.formatLogs(logs, formatter, pollyFormat);
        
        try {
            String pasteUrl = this.paster.paste(logString);
            irc.sendMessage(channel, "Logs (" + logs.size() + "/" + unfilteredSize + 
                " Ergebnisse): " + pasteUrl, this);
        } catch (Exception e) {
            irc.sendMessage(channel, "Fehler beim Hochladen der Logs", this);
        }
    }
}
