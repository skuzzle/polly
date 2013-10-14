package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import de.skuzzle.polly.sdk.DelayedCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.NumberType;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.time.Milliseconds;


public class CrackerCommand extends DelayedCommand {

    private final static int CRACKER_DELAY = (int) Milliseconds.fromMinutes(5);
    private final static int CRACKER_INC = 1;
    
    
    
    public CrackerCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "cracker", CRACKER_DELAY); //$NON-NLS-1$
        this.createSignature(MSG.crackerSig0Desc);
        this.createSignature(MSG.crackerSig1Desc, 
        		new Parameter(MSG.crackerSig1User, Types.USER));
        this.setHelpText(MSG.crackerHelp);
        this.setRegisteredOnly();
    }
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature)
            throws CommandException, InsufficientRightsException {
        
        try {
            
            User user = executer;
            if (this.match(signature, 1)) {
                final String name = signature.getStringValue(0);
                user = this.getMyPolly().users().getUser(name);
                
                if (user == null) {
                    throw new CommandException(MSG.bind(MSG.crackerUnknownUser, name));
                }
            }
            int crackers = this.incCracker(executer, user, CRACKER_INC);
            this.reply(channel, MSG.bind(MSG.crackerSuccess, 
                    crackers, executer.getName()));
        } catch (DatabaseException e) {
           throw new CommandException(e); 
        }
        
        return false;
    }
    
    
    
    private int incCracker(User executor, User user, int amount) 
            throws DatabaseException {
        int crackers = (int) ((NumberType) user.getAttribute(MyPlugin.CRACKER)).getValue();
        crackers += amount;
        try {
            this.getMyPolly().users().setAttributeFor(executor, user, MyPlugin.CRACKER, 
                Integer.toString(crackers));
        } catch (ConstraintException e) {
            throw new RuntimeException("this was thought to be impossibru to happen", e); //$NON-NLS-1$
        }
        return crackers;
    }
}
