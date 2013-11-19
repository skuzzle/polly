package core.output;

import java.util.List;

import polly.logging.MSG;
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
        
        final String logString = this.formatLogs(logs, formatter, pollyFormat);
        
        try {
            final String pasteUrl = this.paster.paste(logString);
            final String result = MSG.bind(MSG.pasteOutputResult,
                    logs.size(), unfilteredSize, pasteUrl);
            irc.sendMessage(channel, result, this);
        } catch (Exception e) {
            irc.sendMessage(channel, MSG.pasteOutputFail, this);
        }
    }
}
