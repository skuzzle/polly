package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;

public class InfoCommand extends Command {

	public InfoCommand(MyPolly polly) throws DuplicatedSignatureException {
		super(polly, "info"); //$NON-NLS-1$
		this.createSignature(MSG.infoSig0Desc, 
		    MyPlugin.INFO_PERMISSION,
            new Parameter(MSG.infoSig0Command, Types.STRING));
		this.createSignature(MSG.infoSig1Desc, 
		    MyPlugin.INFO_PERMISSION,
            new Parameter(MSG.infoSig1User, Types.USER));
		this.setHelpText(MSG.infoHelp);
	}

	
	
	@Override
	protected boolean executeOnBoth(User executer, String channel, Signature signature) {
	    if (this.match(signature, 0)) {
	        final String s = signature.getStringValue(0);

	        try {
	            final Command cmd = this.getMyPolly().commands().getCommand(s);

	            final String result = MSG.bind(MSG.infoCommandInfo, 
	                    cmd.getSignatures().size(),
	                    cmd.isRegisteredOnly(),
	                    cmd.getHelpText());
	            this.reply(channel, result);
		    } catch (UnknownCommandException e) {
		        this.reply(channel, MSG.bind(MSG.infoUnknownCommand, s));
		    }
		} else if (this.match(signature, 1)) {
		    String u = signature.getStringValue(0);
		    User user = this.getMyPolly().users().getUser(u);
		    
		    if (user == null) {
		        this.reply(channel, MSG.bind(MSG.infoUnknownUser, u));
		        return false;
		    }
		    final String result = MSG.bind(MSG.infoUserInfo, 
		            u, user.getCurrentNickName());
		    this.reply(channel, result);
		}
		
		return false;
	}

}
