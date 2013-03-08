package commands;

import java.util.Date;

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
        super(polly, "uptime");
        this.joinTimeCollector = jtc;
        this.createSignature("Zeigt an wie lange polly schon läuft.", 
            MyPlugin.UPTIME_PERMISSION);
        this.createSignature("Zeigt an wie lange der angegebene Benutzer schon online ist.",
            MyPlugin.UPTIME_PERMISSION,
            new Parameter("Nickname", Types.USER));
        this.setHelpText("Zeigt an wie lange polly schon läuft.");
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
                throws CommandException, InsufficientRightsException {
        
        Date start = this.getMyPolly().getStartTime();
        Date now = Time.currentTime();
        FormatManager f = this.getMyPolly().formatting();
        String result = "";
        if (this.match(signature, 0)) {
            long diff = (now.getTime() - start.getTime()) / 1000;
            result = this.getMyPolly().irc().getNickname() + " online seit: " + 
                f.formatDate(start) + " (" + f.formatTimeSpan(diff) + ")";
        } else if (this.match(signature, 1)) {
            String nickName = signature.getStringValue(0);
            Long joinTime = this.joinTimeCollector.getJoinTime(nickName);
            if (joinTime == null) {
                result = nickName + " ist nicht online.";
            } else {
                start = new Date(joinTime);
                long diff = (now.getTime() - start.getTime()) / 1000;
                result = nickName + " online seit: " + 
                    f.formatDate(start) + " (" + f.formatTimeSpan(diff) + ")";
            }
            
            
        }
        this.reply(channel, result);
        return false;
    }

}
