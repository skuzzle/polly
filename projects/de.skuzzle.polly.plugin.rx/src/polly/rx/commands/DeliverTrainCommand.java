package polly.rx.commands;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.TrainBillV2;
import polly.rx.core.TrainManagerV2;
import polly.rx.entities.TrainEntityV3;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class DeliverTrainCommand extends Command {

    private TrainManagerV2 trainManager;
    
    public DeliverTrainCommand(MyPolly polly, TrainManagerV2 trainManager) 
            throws DuplicatedSignatureException {
        super(polly, "deliver"); //$NON-NLS-1$
        this.createSignature(MSG.deliverSig0Desc, 
            MyPlugin.DELIVER_TRAIN_PERMISSION,
            new Parameter(MSG.deliverSig0User, Types.USER));
        this.createSignature(MSG.deliverSig1Desc,
            MyPlugin.DELIVER_TRAIN_PERMISSION,
            new Parameter(MSG.deliverSig1User, Types.USER), 
            new Parameter(MSG.deliverSig1Receiver, Types.USER));

        this.setHelpText(MSG.deliverHelp);
        this.setRegisteredOnly();
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {
        
        String deliverTo = ""; //$NON-NLS-1$
        String userName = ""; //$NON-NLS-1$
        if (this.match(signature, 0)) {
            userName = signature.getStringValue(0);
            deliverTo = userName;
        } else if (this.match(signature, 1)) {
            userName = signature.getStringValue(0);
            deliverTo = signature.getStringValue(1);
        }
        TrainBillV2 bill = this.trainManager.getBill(executer, userName);
        for (TrainEntityV3 train : bill.getTrains()) {
            this.reply(deliverTo, train.format(this.getMyPolly().formatting()));
        }
        this.reply(deliverTo, "========================="); //$NON-NLS-1$
        
        this.reply(deliverTo, bill.toString());
        return false;
    }
}
