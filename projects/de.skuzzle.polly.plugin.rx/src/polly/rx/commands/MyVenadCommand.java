package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class MyVenadCommand extends Command {

    public MyVenadCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "myvenad"); //$NON-NLS-1$
        this.createSignature(MSG.myVenadSig0Desc, 
            new Parameter(MSG.myVenadSig0Name, Types.STRING));
        this.setRegisteredOnly();
        this.setHelpText(MSG.myVenadHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String vname = signature.getStringValue(0);
            
            try {
                this.getMyPolly().users().setAttributeFor(executer, executer, 
                    MyPlugin.VENAD, vname);
                this.reply(channel, MSG.myVenadSuccess);
            } catch (DatabaseException e) {
                throw new CommandException(e);
            } catch (ConstraintException e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }

}
