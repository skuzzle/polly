package polly.rx.commands;

import polly.rx.core.TrainBillV2;
import polly.rx.core.TrainManagerV2;
import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.entities.TrainEntityV3;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.time.Milliseconds;


public class AddTrainCommand extends Command {
    
    private TrainManagerV2 trainManager;

    public AddTrainCommand(MyPolly polly, TrainManagerV2 trainManager) 
            throws DuplicatedSignatureException {
        
        super(polly, "train"); //$NON-NLS-1$
        this.createSignature(MSG.addTrainSig0Desc, 
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter(MSG.addTrainSig0User, Types.USER),
            new Parameter(MSG.addTrainSig0Bill, Types.STRING));
        this.createSignature(MSG.addTrainSig1Desc,
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter(MSG.addTrainSig1User, Types.USER),
            new Parameter(MSG.addTrainSig1Details, Types.BOOLEAN));
        this.createSignature(MSG.addTrainSig2Desc,
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter(MSG.addTrainSig2User, Types.USER),
            new Parameter(MSG.addTrainSig2Bill, Types.STRING),
            new Parameter(MSG.addTrainSig2Weight, Types.NUMBER));
        this.createSignature(MSG.addTrainSig3Desc,
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter(MSG.addTrainSig3User, Types.USER));
        this.setHelpText(MSG.addTrainHelp);
        this.setRegisteredOnly();
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException {

        double mod = 1.0;
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            
            this.addTrain(executer, channel, userName, train, mod);
        } else if (this.match(signature, 2)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            mod = signature.getNumberValue(2);
            
            this.addTrain(executer, channel, userName, train, mod);
        } else if (this.match(signature, 1)) {
            String userName = signature.getStringValue(0);
            boolean showAll = signature.getBooleanValue(1);
            
            TrainBillV2 bill = this.trainManager.getBill(executer, userName);
            this.outputTrain(showAll, bill, channel);
        } else if (this.match(signature, 3)) {
            String userName = signature.getStringValue(0);
            
            TrainBillV2 bill = this.trainManager.getBill(executer, userName);

            this.outputTrain(false, bill, channel);
        }
        return false;
    }
    
    
    
    private void outputTrain(boolean detailed, TrainBillV2 bill, String channel) {
        if (detailed) {
            for (TrainEntityV3 train : bill.getTrains()) {
                this.reply(channel, train.format(this.getMyPolly().formatting()));
            }
            this.reply(channel, "========================="); //$NON-NLS-1$
        }
        
        this.reply(channel, bill.toString());
    }
    
    
    
    private void addTrain(User trainer, String channel, String forUser, 
        String train, double mod) throws CommandException {
        
        try {
            TrainEntityV3 te = TrainEntityV3.parseString(trainer.getId(), forUser, mod, train);
            final TrainBillV2 bill = this.trainManager.addTrain(te, trainer);
            if (te.getDuration() != 0) {
                // HACK: this requires the Remind Plugin to be installed and running!
                final String command = MSG.bind(MSG.addTrainRemind, 
                        te.getForUser(),
                        bill.weightedSum(), Milliseconds.toSeconds(te.getDuration()));
                this.getMyPolly().commands().executeString(
                        command, 
                        trainer.getCurrentNickName(), 
                        true, trainer, this.getMyPolly().irc());
            }
            this.reply(channel, MSG.bind(MSG.addTrainSuccess, bill.weightedSum()));
        } catch (Exception e) {
            throw new CommandException(MSG.addTrainFail, e);
        }
    }
}
