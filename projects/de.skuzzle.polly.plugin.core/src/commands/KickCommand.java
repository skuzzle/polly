package commands;

import java.util.List;

import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class KickCommand extends Command {

	public KickCommand(MyPolly polly) throws DuplicatedSignatureException {
		super(polly, "kick");
		this.createSignature("Kickt den angegebenen User aus dem aktuellen Channel", 
		    MyPlugin.KICK_PERMISSION,
				new Parameter("User", Types.USER));
		this.createSignature("Kickt den angegebenen User aus dem aktuellen Channel " +
				"mit dem angegebenen Grund",
				MyPlugin.KICK_PERMISSION,
				new Parameter("User", Types.USER), 
				new Parameter("Grund", Types.STRING));
		this.createSignature("Kickt den angegebenen User aus dem angegebenen Channel",
		    MyPlugin.KICK_PERMISSION,
				new Parameter("Channel", Types.CHANNEL), 
				new Parameter("User", Types.USER));	
		this.createSignature("Kickt den angegebenen User aus dem angegebenen Channel " +
				"mit dem angegebenen Grund",
				MyPlugin.KICK_PERMISSION,
				new Parameter("Channel", Types.CHANNEL), 
				new Parameter("User", Types.USER), 
				new Parameter("Grund", Types.STRING));	
		this.createSignature("Kickt alle angegebenen Benutzer aus dem aktuellen Channel", 
		    new Parameter("Userliste", new Types.ListType(Types.USER)));
        this.createSignature("Kickt alle angegebenen Benutzer aus dem angegebenen Channel", 
            new Parameter("Userliste", new Types.ListType(Types.USER)),
            new Parameter("Channel", Types.CHANNEL));
		this.setRegisteredOnly();
		this.setUserLevel(UserManager.ADMIN);
		this.setHelpText("Befehl zum Kicken von Benutzern.");
	}
	
	
	
	@Override
	protected boolean executeOnBoth(User executer, String channel,
	        Signature signature) {
	    return true;
	}
	
	
	
	@Override
	protected void executeOnChannel(User executer, String channel,
			Signature signature) {
		if (this.match(signature, 0)) {
			String user = signature.getStringValue(0);
			this.getMyPolly().irc().kick(channel, user, "");
			
		} else if (this.match(signature, 1)) {
			String user = signature.getStringValue(0);
			String reason = signature.getStringValue(1);
			this.getMyPolly().irc().kick(channel, user, reason);
			
		} else if(this.match(signature, 2)) {
			String from = signature.getStringValue(0);
			String user = signature.getStringValue(1);
			this.getMyPolly().irc().kick(from, user, "");
			
		} else if (this.match(signature, 3)) {
			String from = signature.getStringValue(0);
			String user = signature.getStringValue(1);
			String reason = signature.getStringValue(2);
			this.getMyPolly().irc().kick(from, user, reason);
		} else if (this.match(signature, 4)) {
		    List<Types.UserType> users = signature.getListValue(Types.UserType.class, 0);
		    for (final UserType user : users) {
		        this.getMyPolly().irc().kick(channel, user.getValue(), "");
		    }
		} else if (this.match(signature, 5)) {
            List<Types.UserType> users = signature.getListValue(Types.UserType.class, 0);
            String from = signature.getStringValue(1);
            for (final UserType user : users) {
                this.getMyPolly().irc().kick(from, user.getValue(), "");
            }
        }
	}
	
	
	
	@Override
	protected void executeOnQuery(User executer, Signature signature) {
		if (signature.getId() > 1) {
			this.executeOnChannel(executer, executer.getCurrentNickName(), signature);
		}
	}
}
