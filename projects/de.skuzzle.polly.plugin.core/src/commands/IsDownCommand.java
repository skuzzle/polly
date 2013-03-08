package commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import polly.core.MyPlugin;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;



public class IsDownCommand extends Command {

    public IsDownCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "isdown");
        this.createSignature("Dieser Befehl checkt ob eine Webseite derzeit erreichbar " +
        		"ist.", 
    		MyPlugin.ISDOWN_PERMISSION,
    		new Parameter("URL", Types.STRING));
        this.createSignature("Dieser Befehl checkt ob eine Webseite innerhalb eines " +
        		"Timeouts (in ms) erreichbar ist.", 
    		MyPlugin.ISDOWN_PERMISSION,
    		new Parameter("URL", Types.STRING),
    		new Parameter("Timeout", Types.NUMBER));
        this.setHelpText("Überprüft ob eine Webseite erreichbar ist");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
            throws CommandException, InsufficientRightsException {
        
        String url = signature.getStringValue(0);
        url = url.startsWith("http://") ? url : "http://" + url;
        
        int timeout = 5000;
        if (this.match(signature, 1)) {
            timeout = (int) signature.getNumberValue(1);
        }
        
        try {
            URL u = new URL(url);
            URLConnection c = u.openConnection();
            c.setConnectTimeout(timeout);
            c.connect();
            this.reply(channel, url + " ist erreichbar");
        } catch (MalformedURLException e) {
            this.reply(channel, url + " ist keine gültige URL");
        } catch (UnknownHostException e) {
            this.reply(channel, "Unbekannter host: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            this.reply(channel, url + " ist nicht erreichbar");
        }
        return false;
    }

}
