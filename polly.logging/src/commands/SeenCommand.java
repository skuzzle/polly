package commands;

import java.util.Collections;

import polly.logging.MyPlugin;

import core.DefaultLogFormatter;
import core.LogFormatter;
import core.PollyLoggingManager;
import core.output.IrcLogOutput;
import core.output.LogOutput;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.LogEntry;

public class SeenCommand extends AbstractLogCommand {
    
    public SeenCommand(MyPolly polly, PollyLoggingManager logManager) 
            throws DuplicatedSignatureException {
        super(polly, "seen", logManager);
        this.createSignature("Zeigt an wann ein Benutzer das letzte mal gesehen wurde.", 
            MyPlugin.SEEN_PERMISSION,
            new Parameter("User", Types.USER));
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String user = signature.getStringValue(0);
            try {
                LogEntry le = this.logManager.seenUser(user);
                
                LogFormatter lf = new DefaultLogFormatter();
                LogOutput lo = new IrcLogOutput();
                if (!this.getMyPolly().irc().isOnChannel(le.getChannel(), 
                        executer.getCurrentNickName())) {
                    le = LogEntry.forMessage(le.getNickname(), "<hidden>", 
                        le.getChannel(), le.getDate());
                }
                lo.outputLogs(this.getMyPolly().irc(), channel, 
                    Collections.singletonList(le), 1, lf, this.getMyPolly().formatting());
            } catch (Exception e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }

}
