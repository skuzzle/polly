package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class VenadCommand extends Command {

    public VenadCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "venad"); //$NON-NLS-1$
        this.createSignature(MSG.venadSig0Desc, 
                new Parameter(MSG.venadSig0User,Types.USER));
        this.setHelpText(MSG.venadHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            User u = this.getMyPolly().users().getUser(userName);
            
            if (u == null) {
                this.reply(channel, MSG.bind(MSG.venadUnknownUser, userName));
                return false;
            }
            
            this.reply(channel, MSG.bind(MSG.venadSuccess, userName, 
                u.getAttribute(MyPlugin.VENAD).valueString(this.getMyPolly().formatting()))); 
        }
        return false;
    }
}