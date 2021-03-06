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

public class ClumBombCommand extends Command {

	public ClumBombCommand(MyPolly polly) throws DuplicatedSignatureException {
		super(polly, "clumbomb"); //$NON-NLS-1$
		this.createSignature(MSG.clumBombSig0Desc, 
		    MyPlugin.CLUMBOMB_PERMISSION,
		    new Parameter(MSG.userName,Types.USER), 
		    new Parameter(MSG.clumBombSig0Amount, Types.NUMBER));
		this.setRegisteredOnly();
		this.setHelpText(MSG.clumBombHelp);
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
			int i = (int) signature.getNumberValue(1);
			i = Math.min(10, i);
			
			for (int j = 0; j < i; j++) {
				this.reply(channel, user);
			}
		}
	}
}