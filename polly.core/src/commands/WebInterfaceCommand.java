package commands;

import java.io.IOException;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;
import de.skuzzle.polly.sdk.roles.RoleManager;

public class WebInterfaceCommand extends Command {

    public WebInterfaceCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "web");
        this.createSignature("Gibt den Link zum polly Webinterface aus");
        this.createSignature("Aktiviert/Deaktiviert das Webinterface.", 
            RoleManager.ADMIN_PERMISSION, 
            new Parameter("An/Aus", Types.BOOLEAN));
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
        } else if (this.match(signature, 1)) {
            boolean newState = signature.getBooleanValue(0);
            if (this.getMyPolly().web().isRunning() && !newState) {
                this.getMyPolly().web().stopWebServer();
            } else if (!this.getMyPolly().web().isRunning() && newState) {
                try {
                    this.getMyPolly().web().startWebServer();
                } catch (IOException e) {
                    throw new CommandException(e);
                }
            }
        }
        
        return false;
    }

}
