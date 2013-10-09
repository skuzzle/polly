package commands;

import java.util.List;

import polly.core.MSG;
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
		this.createSignature(MSG.kickSig0Desc, 
		    MyPlugin.KICK_PERMISSION,
				new Parameter(MSG.kickSig0User, Types.USER));
		this.createSignature(MSG.kickSig1Desc,
				MyPlugin.KICK_PERMISSION,
				new Parameter(MSG.kickSig1User, Types.USER), 
				new Parameter(MSG.kickSig1Reason, Types.STRING));
		this.createSignature(MSG.kickSig2Desc,
		    MyPlugin.KICK_PERMISSION,
				new Parameter(MSG.kickSig2Channel, Types.CHANNEL), 
				new Parameter(MSG.kickSig2User, Types.USER));	
		this.createSignature(MSG.kickSig3Desc,
				MyPlugin.KICK_PERMISSION,
				new Parameter(MSG.kickSig3Channel, Types.CHANNEL), 
				new Parameter(MSG.kickSig3User, Types.USER), 
				new Parameter(MSG.kickSig3Reason, Types.STRING));	
		this.createSignature(MSG.kickSig4Desc, 
		    new Parameter(MSG.kickSig4Users, new Types.ListType(Types.USER)));
        this.createSignature(MSG.kickSig5Desc, 
            new Parameter(MSG.kickSig5Users, new Types.ListType(Types.USER)),
            new Parameter(MSG.kickSig5Channel, Types.CHANNEL));
		this.setRegisteredOnly();
		this.setHelpText(MSG.kickHelp);
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
