package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.TrainManagerV2;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class CloseTrainCommand extends Command {

    private TrainManagerV2 trainManager;
    
    public CloseTrainCommand(MyPolly polly, TrainManagerV2 trainManager) 
            throws DuplicatedSignatureException {
        super(polly, "closetrain"); //$NON-NLS-1$
        this.createSignature(MSG.closeTrainSig0Desc, 
            MyPlugin.CLOSE_TRAIN_PERMISSION,
            new Parameter(MSG.closeTrainSig0User, Types.USER));
        this.createSignature(MSG.closeTrainSig1Desc, 
            MyPlugin.CLOSE_TRAIN_PERMISSION,
            new Parameter(MSG.closeTrainSig1Id, Types.NUMBER));
        this.setRegisteredOnly();
        this.setHelpText(MSG.closeTrainHelp);
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            try {
                this.trainManager.closeOpenTrains(executer, userName);
                this.reply(channel, MSG.bind(MSG.closeTrainSuccessAll, userName));
                
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        } else if (this.match(signature, 1)) {
            int id = (int) signature.getNumberValue(0);
            try {
                this.trainManager.closeOpenTrain(executer, id);
                this.reply(channel, MSG.bind(MSG.closeTrainSuccessSingle, id));
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }
}
