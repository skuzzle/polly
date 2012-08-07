package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;

public class WebInterfaceCommand extends Command {

    public WebInterfaceCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "web");
        this.createSignature("Gibt den Link zum polly Webinterface aus");
        this.setHelpText("Gibt den Link zum polly Webinterface aus");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String url = "http://" + this.getMyPolly().web().getPublicHost() + ":" + 
                    this.getMyPolly().web().getPort();
            
            if (this.getMyPolly().web().isRunning()) {
                this.reply(channel, "Polly Webinterface: " + url);
            } else {
                this.reply(channel, "Webinterface ist zurzeit abgeschaltet.");
            }
        }
        
        return false;
    }

}
