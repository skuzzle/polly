package commands;

import java.util.List;

import polly.core.Messages;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class KickCommand extends Command {

	public KickCommand(MyPolly polly) throws DuplicatedSignatureException {
		super(polly, "kick"); //$NON-NLS-1$
		this.createSignature(Messages.kickSig0Desc, 
		    MyPlugin.KICK_PERMISSION,
				new Parameter(Messages.kickSig0User, Types.USER));
		this.createSignature(Messages.kickSig1Desc,
				MyPlugin.KICK_PERMISSION,
				new Parameter(Messages.kickSig1User, Types.USER), 
				new Parameter(Messages.kickSig1Reason, Types.STRING));
		this.createSignature(Messages.kickSig2Desc,
		    MyPlugin.KICK_PERMISSION,
				new Parameter(Messages.kickSig2Channel, Types.CHANNEL), 
				new Parameter(Messages.kickSig2User, Types.USER));	
		this.createSignature(Messages.kickSig3Desc,
				MyPlugin.KICK_PERMISSION,
				new Parameter(Messages.kickSig3Channel, Types.CHANNEL), 
				new Parameter(Messages.kickSig3User, Types.USER), 
				new Parameter(Messages.kickSig3Reason, Types.STRING));	
		this.createSignature(Messages.kickSig4Desc, 
		    new Parameter(Messages.kickSig4Users, new Types.ListType(Types.USER)));
        this.createSignature(Messages.kickSig5Desc, 
            new Parameter(Messages.kickSig5Users, new Types.ListType(Types.USER)),
            new Parameter(Messages.kickSig5Channel, Types.CHANNEL));
		this.setRegisteredOnly();
		this.setHelpText(Messages.kickHelp);
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
			this.getMyPolly().irc().kick(channel, user, ""); //$NON-NLS-1$
			
		} else if (this.match(signature, 1)) {
			String user = signature.getStringValue(0);
			String reason = signature.getStringValue(1);
			this.getMyPolly().irc().kick(channel, user, reason);
			
		} else if(this.match(signature, 2)) {
			String from = signature.getStringValue(0);
			String user = signature.getStringValue(1);
			this.getMyPolly().irc().kick(from, user, ""); //$NON-NLS-1$
			
		} else if (this.match(signature, 3)) {
			String from = signature.getStringValue(0);
			String user = signature.getStringValue(1);
			String reason = signature.getStringValue(2);
			this.getMyPolly().irc().kick(from, user, reason);
		} else if (this.match(signature, 4)) {
		    List<Types.UserType> users = signature.getListValue(Types.UserType.class, 0);
		    for (final UserType user : users) {
		        this.getMyPolly().irc().kick(channel, user.getValue(), ""); //$NON-NLS-1$
		    }
		} else if (this.match(signature, 5)) {
            List<Types.UserType> users = signature.getListValue(Types.UserType.class, 0);
            String from = signature.getStringValue(1);
            for (final UserType user : users) {
                this.getMyPolly().irc().kick(from, user.getValue(), ""); //$NON-NLS-1$
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
