package commands;

import java.util.List;

import polly.logging.MyPlugin;

import core.PollyLoggingManager;
import core.filters.AnyLogFilter;
import core.filters.ChainedLogFilter;
import core.filters.DateLogFilter;
import core.filters.MessageRegexLogFilter;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import entities.LogEntry;


public class ChannelLogCommand extends AbstractLogCommand {
    
    public ChannelLogCommand(MyPolly polly, PollyLoggingManager logManager) 
                throws DuplicatedSignatureException {
        super(polly, "log", logManager);
        this.createSignature("Filtert Log Einträge eines Channels", 
            MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter("Channel", Types.CHANNEL));
        this.createSignature("Filtert Log Einträge eines Channels mit bestimmten Inhalt", 
            MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter("Channel", Types.CHANNEL), 
            new Parameter("Pattern", Types.STRING));
        this.createSignature("Filtert Log Einträge eines Channels mit bestimmten Inhalt " +
        		"die nicht älter sind als das angegebne Datum", 
    		MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter("Channel", Types.CHANNEL),
            new Parameter("Pattern", Types.STRING), 
            new Parameter("Datum", Types.DATE));
        this.createSignature("Filtert Log Einträge eines Channels mit bestimmten Inhalt " +
        		"die zweichen den Angegebenen Zeitpunkten liegen",
    		MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter("Channel", Types.CHANNEL),
            new Parameter("Pattern", Types.STRING), 
            new Parameter("Von", Types.DATE),
            new Parameter("Bis", Types.DATE));
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        ChainedLogFilter filter = new ChainedLogFilter(new AnyLogFilter());
        String c = signature.getStringValue(0);

        // All signatures except the first have a message pattern
        if (signature.getId() > 0) {
            String pattern = signature.getStringValue(1);
            filter.addFilter(new MessageRegexLogFilter(pattern));
        }
        
        
        List<LogEntry> prefiltered = null;
        
        try {
            prefiltered = this.logManager.preFilterChannel(c);

            if (this.match(signature, 2)) {
                filter.addFilter(new DateLogFilter(signature.getDateValue(2)));
            } else if (this.match(signature, 3)) {
                filter.addFilter(new DateLogFilter(signature.getDateValue(2), 
                    signature.getDateValue(3)));
            }
            
            prefiltered = this.logManager.postFilter(prefiltered, filter);
            
            // output answer in query
            this.logManager.outputLogResults(this.getMyPolly(), executer, prefiltered, 
                    executer.getCurrentNickName());
            
            
        } catch (DatabaseException e) {
            throw new CommandException(e);
        }
        
        return false;
    }
}
