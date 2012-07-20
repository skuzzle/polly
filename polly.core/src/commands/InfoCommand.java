package commands;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.model.User;

public class InfoCommand extends Command {

	public InfoCommand(MyPolly polly) throws DuplicatedSignatureException {
		super(polly, "info");
		this.createSignature("Gibt Informationen über den angegebenen Befehl aus", 
		    MyPlugin.INFO_PERMISSION,
            new Parameter("Befehl", Types.COMMAND));
		this.createSignature("Gibt Informationen über den angegebenen Benutzer aus.", 
		    MyPlugin.INFO_PERMISSION,
            new Parameter("User", Types.USER));
		this.setHelpText("Gibt Informationen über andere Befehle oder " +
				"Benutzer aus.");
	}

	
	
	@Override
	protected boolean executeOnBoth(User executer, String channel, Signature signature) {
        StringBuilder b = new StringBuilder();

	    if (this.match(signature, 0)) {
	        String s = signature.getStringValue(0);

	        try {
	            Command cmd = this.getMyPolly().commands().getCommand(s);

	            b.append("Signaturen: ");
	            b.append(cmd.getSignatures().size());
	            b.append(", User-Level: ");
	            b.append(cmd.getUserLevel());
	            b.append(", nur Registrierte: ");
	            b.append(cmd.isRegisteredOnly() ? "ja" : "nein");
	            b.append(", Beschreibung: ");
	            b.append(cmd.getHelpText());
	            this.reply(channel, b.toString());
		    } catch (UnknownCommandException e) {
		        this.reply(channel, "Befehl '" + s + "' existiert nicht.");
		    }
		} else if (this.match(signature, 1)) {
		    String u = signature.getStringValue(0);
		    User user = this.getMyPolly().users().getUser(u);
		    
		    if (user == null) {
		        this.reply(channel, "Benutzer '" + u + "' existiert nicht.");
		        return false;
		    }
		    
		    b.append("Name: ");
		    b.append(u);
		    b.append(", aktueller Nickname: ");
		    b.append(user.getCurrentNickName());
		    this.reply(channel, b.toString());
		}
		
		return false;
	}

}
