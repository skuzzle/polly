package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownUserException;
import de.skuzzle.polly.sdk.model.User;


public class GhostCommand extends Command {

    public GhostCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "ghost");
        this.createSignature("Loggt den angegebenen Benutzer aus.", 
            new Types.UserType(), new Types.StringType());
        this.setHelpText("Mit diesem Befehl kannst du einen Benutzer bei polly " +
        		"ausloggen. Gib dafür den Benutzer und sein Passwort an");
        this.setQryCommand(true);
        
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
        Signature signature) throws CommandException {
        this.reply(channel, "Nur per Query möglich!");
    }
    
    
    protected void executeOnQuery(User executer, Signature signature) 
            throws CommandException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String pw = signature.getStringValue(1);
            
            User user = this.getMyPolly().users().getUser(
                new IrcUser(executer.getCurrentNickName(), "", ""));
            
            if (user == null) {
                this.reply(executer, "Benutzer '" + userName + "' ist nicht angemeldet");
            } else if (!user.checkPassword(pw)) {
                this.reply(executer, "Das angegebene Passwort ist falsch");
            } else {
                try {
                    this.getMyPolly().users().logoff(user);
                    this.reply(executer, "Benutzer '" + userName + "' wurde ausgeloggt");
                } catch (UnknownUserException e) {
                    throw new CommandException(
                        "Unerwarteter Fehler: " + e.getMessage(), e);
                }
            }
            
        }
    };
}