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


public class UserLogCommand extends AbstractLogCommand {

    public UserLogCommand(MyPolly polly, PollyLoggingManager logManager) 
                throws DuplicatedSignatureException {
        super(polly, "ulog", logManager); //$NON-NLS-1$
        this.createSignature(MSG.userLogSig0Desc, 
            MyPlugin.USER_LOG_PERMISSION,
            new Parameter(MSG.userLogSig0User, Types.STRING));
        this.createSignature(MSG.userLogSig1Desc,
            MyPlugin.USER_LOG_PERMISSION,
            new Parameter(MSG.userLogSig1User, Types.STRING), 
            new Parameter(MSG.userLogSig1Pattern, Types.STRING));
        this.createSignature(MSG.userLogSig2Desc,
            MyPlugin.USER_LOG_PERMISSION,
            new Parameter(MSG.userLogSig2User, Types.STRING), 
            new Parameter(MSG.userLogSig2Pattern, Types.STRING),
            new Parameter(MSG.userLogSig2Limit, Types.NUMBER));
        this.createSignature(MSG.userLogSig3Desc, 
    		MyPlugin.USER_LOG_PERMISSION,
            new Parameter(MSG.userLogSig3User, Types.STRING), 
            new Parameter(MSG.userLogSig3Pattern, Types.STRING),
            new Parameter(MSG.userLogSig3Date, Types.DATE));
        this.createSignature(MSG.userLogSig4Desc,
    		MyPlugin.USER_LOG_PERMISSION,
            new Parameter(MSG.userLogSig4User, Types.STRING), 
            new Parameter(MSG.userLogSig4Pattern, Types.STRING),
            new Parameter(MSG.userLogSig4From, Types.DATE),
            new Parameter(MSG.userLogSig4To, Types.DATE));
        this.setHelpText(MSG.userLogHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        ChainedLogFilter filter = new ChainedLogFilter(new AnyLogFilter());
        String user = signature.getStringValue(0);

        // All signatures except the first have a message pattern
        if (signature.getId() > 0) {
            String pattern = signature.getStringValue(1);
            filter.addFilter(new MessageRegexLogFilter(pattern));
        }
        
        
        List<LogEntry> prefiltered = null;
        
        try {
            prefiltered = this.logManager.filterUserRegex(user);

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
