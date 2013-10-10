package commands;

import java.util.List;

import polly.logging.MSG;
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
        super(polly, "log", logManager); //$NON-NLS-1$
        this.createSignature(MSG.logSig0Desc, 
            MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter(MSG.logSig0Channel, Types.CHANNEL));
        this.createSignature(MSG.logSig1Desc, 
            MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter(MSG.logSig1Channel, Types.CHANNEL), 
            new Parameter(MSG.logSig1Pattern, Types.STRING));
        this.createSignature(MSG.logSig2Desc, 
    		MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter(MSG.logSig2Channel, Types.CHANNEL),
            new Parameter(MSG.logSig2Pattern, Types.STRING), 
            new Parameter(MSG.logSig2Date, Types.DATE));
        this.createSignature(MSG.logSig3Desc,
    		MyPlugin.CHANNEL_LOG_PERMISSION,
            new Parameter(MSG.logSig3Channel, Types.CHANNEL),
            new Parameter(MSG.logSig3Pattern, Types.STRING), 
            new Parameter(MSG.logSig3From, Types.DATE),
            new Parameter(MSG.logSig3To, Types.DATE));
        this.createSignature(MSG.logSig4Desc, 
            new Parameter(MSG.logSig4Pattern, Types.STRING));
        this.createSignature(MSG.logSig5Desc);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        // HACK HACK
        String chan = channel;
        if (signature.getId() < 4) {
            // all signatures except 4 and 5 have channel as the first parameter
            chan = signature.getStringValue(0);
        }
        
        ChainedLogFilter filter = new ChainedLogFilter(new AnyLogFilter());

        // All signatures except the first have a message pattern
        if (signature.getId() > 0 && signature.getId() < 5) {
            int paramIdx = 1;
            if (signature.getId() == 4) {
                // signature 4 has pattern as first parameter
                paramIdx = 0;
            }
            String pattern = signature.getStringValue(paramIdx);
            filter.addFilter(new MessageRegexLogFilter(pattern));
        }
        
        
        List<LogEntry> prefiltered = null;
        
        try {
            prefiltered = this.logManager.preFilterChannel(chan);

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
