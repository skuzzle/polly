package commands;

import java.util.Date;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class UptimeCommand extends Command {

    public UptimeCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "uptime");
        this.createSignature("Zeigt an wie lange polly schon läuft.");
        this.setHelpText("Zeigt an wie lange polly schon läuft.");
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
                throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            Date start = this.getMyPolly().getStartTime();
            Date now = this.getMyPolly().pollySystemTime();
            FormatManager f = this.getMyPolly().formatting();
            long diff = (now.getTime() - start.getTime()) / 1000;
            String result = "Polly online seit: " + 
                f.formatDate(start) + " (" + f.formatTimeSpan(diff) + ")";
            this.reply(channel, result);
        }
        return false;
    }

}
