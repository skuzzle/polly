package commands;

import java.io.IOException;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
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
        boolean ssl = false;
        try {
            ssl = this.getMyPolly().configuration().open("http.cfg").readBoolean(
                    Configuration.HTTP_USE_SSL);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        final int port = this.getMyPolly().webInterface().getPort();
        String url = "http" + (ssl ? "s" : "");
        url += "://" + this.getMyPolly().webInterface().getPublicHost();
        url += port == 80 ? "" : ":" + port;
        
        
        if (this.match(signature, 0)) {
            if (this.getMyPolly().webInterface().getServer().isRunning()) {
                this.reply(channel, "Polly Webinterface: " + url);
            } else {
                this.reply(channel, "Webinterface ist zurzeit abgeschaltet.");
            }
        } else if (this.match(signature, 1)) {
            boolean newState = signature.getBooleanValue(0);
            if (this.getMyPolly().webInterface().getServer().isRunning() && !newState) {
                this.getMyPolly().webInterface().getServer().shutdown(0);
                this.reply(channel, "Webserver abgeschaltet");
            } else if (!this.getMyPolly().webInterface().getServer().isRunning() && newState) {
                try {
                    this.getMyPolly().webInterface().getServer().start();
                    this.reply(channel, "Webserver angeschaltet. URL: " + url);
                } catch (IOException e) {
                    throw new CommandException(e);
                }
            }
        }
        
        return false;
    }

}
