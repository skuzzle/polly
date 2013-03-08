package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.AlreadySignedOnException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;



public class AuthCommand extends Command {

    public AuthCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "auth");
        this.createSignature("Meldet den Benutzer bei Polly an", 
            new Parameter("Username", Types.USER),
            new Parameter("Passwort", Types.STRING));
        this.createSignature("Meldet den Benutzer bei Polly an", 
            new Parameter("Passwort", Types.STRING));
        this.setHelpText("Befehl um dich bei Polly anzumelden.");
        this.setQryCommand(true);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        
        this.reply(channel, "Dieser Befehl ist nur im Query ausführbar. " +
        		"Zudem solltest du dein Passwort ändern sofern du es hier für jeden " +
        		"lesbar angegeben hast.");
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        String userName = "";
        String password = "";
        if (this.match(signature, 0)) {
            userName = signature.getStringValue(0);
            password = signature.getStringValue(1);
        } else if (this.match(signature, 1)) {
            userName = executer.getCurrentNickName();
            password = signature.getStringValue(0);
        }
        User user = null;
        try {
            user = this.getMyPolly().users().logon(executer.getCurrentNickName(), 
                    userName, password);
            
            if (user == null) {
                this.reply(executer, "Falsches Passwort.");
                return;
            }
            this.reply(executer, "Du bist jetzt angemeldet.");
        } catch (UnknownUserException e) {
            this.reply(executer, "Unbekannter Benutzername: " + userName);
        } catch (AlreadySignedOnException e) {
            this.reply(executer, "Der Benutzer ist bereits angemeldet.");
        }
    }
}
    