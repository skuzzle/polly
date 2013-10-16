package commands;

import java.util.Date;

import polly.core.MSG;
import polly.core.MyPlugin;
import core.JoinTimeCollector;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.time.Time;


public class UptimeCommand extends Command {

    private JoinTimeCollector joinTimeCollector;
    
    public UptimeCommand(MyPolly polly, JoinTimeCollector jtc) 
                throws DuplicatedSignatureException {
        super(polly, "uptime"); //$NON-NLS-1$
        this.joinTimeCollector = jtc;
        this.createSignature(MSG.uptimeSig0Desc.s, 
            MyPlugin.UPTIME_PERMISSION);
        this.createSignature(MSG.uptimeSig1Desc.s,
            MyPlugin.UPTIME_PERMISSION,
            new Parameter(MSG.uptimeSig1Nick.s, Types.USER));
        this.setHelpText(MSG.uptimeHelp.s);
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
                throws CommandException, InsufficientRightsException {
        
        Date start = this.getMyPolly().getStartTime();
        Date now = Time.currentTime();
        FormatManager f = this.getMyPolly().formatting();
        String result = ""; //$NON-NLS-1$
        if (this.match(signature, 0)) {
            final long diff = now.getTime() - start.getTime();
            
            result = MSG.uptimeOnlineSince.s(this.getMyPolly().irc().getNickname(),
                    f.formatDate(start), f.formatTimeSpanMs(diff));
        } else if (this.match(signature, 1)) {
            String nickName = signature.getStringValue(0);
            Long joinTime = this.joinTimeCollector.getJoinTime(nickName);
            if (joinTime == null) {
                result = MSG.uptimeOffline.s(nickName);
            } else {
                start = new Date(joinTime);
                final long diff = now.getTime() - start.getTime();
                result = MSG.uptimeOnlineSince.s(nickName,
                        f.formatDate(start), f.formatTimeSpanMs(diff));
            }
        }
        this.reply(channel, result);
        return false;
    }

}
