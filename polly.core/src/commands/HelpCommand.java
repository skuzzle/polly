package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.model.User;import de.skuzzle.polly.sdk.Types.CommandType;
import de.skuzzle.polly.sdk.Types.NumberType;



public class HelpCommand extends Command {

	public HelpCommand(MyPolly polly) throws DuplicatedSignatureException {
		super(polly, "help");
		this.createSignature("Gibt die Beschreibung eines Befehls aus.", 
				new CommandType());
		this.createSignature("Gibt die Beschreibung der angegebenen Signatur des " +
				"angegebenen Befehls aus.", new CommandType(), new NumberType());
        this.createSignature("");
		this.setHelpText("Gib ':help :<befehl>' ein um Hilfe zu einem Befehl zu " +
				"bekommen. Gib :cmds ein um eine Liste der möglichen Befehle " +
				"anzuzeigen.");
	}

	

	@Override
	public boolean executeOnBoth(User executer, String channel,
			Signature signature) {
	    try {
    		if (this.match(signature, 0)) {
    			String cmdName = signature.getStringValue(0);
    			Command cmd = this.polly.commands().getCommand(cmdName);
    			this.reply(channel, cmd.getHelpText());
    			
    		} else if (this.match(signature, 1)) {
    			String cmdName = signature.getStringValue(0);
    			int id = (int) signature.getNumberValue(1);
    			
    			Command cmd = this.polly.commands().getCommand(cmdName);
    			this.reply(channel, cmd.getHelpText(id));
    			
    		} else if (this.match(signature, 2)) {
    			this.reply(channel, this.getHelpText());
    		} 
	    } catch (UnknownCommandException e) {
	        this.reply(channel, "Der Befehl '" + e.getMessage() + "' existiert nicht.");
	    }
		return false;
	}
}