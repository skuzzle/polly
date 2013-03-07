package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.model.User;


public class SignOffCommand extends Command {

    public SignOffCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "signoff");
        this.createSignature("Meldet den Benutzer ab.");
        this.setRegisteredOnly();
        this.setHelpText("Befehl um dich bei Polly abzumelden.");
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        try {
            this.getMyPolly().users().logoff(executer);
            this.reply(channel, "Du wurdest abgemeldet.");
        } catch (UnknownUserException e) {
            e.printStackTrace();
        }
        return false;
    }
}
