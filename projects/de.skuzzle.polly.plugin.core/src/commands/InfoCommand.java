package commands;

import polly.core.Messages;
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
		this.createSignature(Messages.infoSig0Desc, 
		    MyPlugin.INFO_PERMISSION,
            new Parameter(Messages.infoSig0Command, Types.STRING));
		this.createSignature(Messages.infoSig1Desc, 
		    MyPlugin.INFO_PERMISSION,
            new Parameter(Messages.infoSig1User, Types.USER));
		this.setHelpText(Messages.infoHelp);
	}

	
	
	@Override
	protected boolean executeOnBoth(User executer, String channel, Signature signature) {
	    if (this.match(signature, 0)) {
	        final String s = signature.getStringValue(0);

	        try {
	            final Command cmd = this.getMyPolly().commands().getCommand(s);

	            final String result = Messages.bind(Messages.infoCommandInfo, 
	                    cmd.getSignatures().size(),
	                    cmd.isRegisteredOnly(),
	                    cmd.getHelpText());
	            this.reply(channel, result);
		    } catch (UnknownCommandException e) {
		        this.reply(channel, Messages.bind(Messages.infoUnknownCommand, s));
		    }
		} else if (this.match(signature, 1)) {
		    String u = signature.getStringValue(0);
		    User user = this.getMyPolly().users().getUser(u);
		    
		    if (user == null) {
		        this.reply(channel, Messages.bind(Messages.infoUnknownUser, u));
		        return false;
		    }
		    final String result = Messages.bind(Messages.infoUserInfo, 
		            u, user.getCurrentNickName());
		    this.reply(channel, result);
		}
		
		return false;
	}

}
