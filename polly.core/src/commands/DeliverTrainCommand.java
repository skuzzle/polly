package commands;

import core.TrainBillV2;
import core.TrainManagerV2;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.TrainEntityV2;

public class DeliverTrainCommand extends Command {

    private TrainManagerV2 trainManager;
    
    public DeliverTrainCommand(MyPolly polly, TrainManagerV2 trainManager) 
            throws DuplicatedSignatureException {
        super(polly, "deliver");
        this.createSignature("Liefert eine Capi-Train Rechnung aus.", 
            new Parameter("User", Types.USER));
        this.createSignature("Liefert eine Capi-Train Rechnung aus.", 
            new Parameter("Benutzer", Types.USER), 
            new Parameter("Empfänger", Types.USER));

        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {
        
        String deliverTo = "";
        String userName = "";
        if (this.match(signature, 0)) {
            userName = signature.getStringValue(0);
            deliverTo = userName;
        } else if (this.match(signature, 1)) {
            userName = signature.getStringValue(0);
            deliverTo = signature.getStringValue(1);
        }
        int trainerId = this.trainManager.getTrainerId(executer.getCurrentNickName());
        TrainBillV2 bill = this.trainManager.getBill(trainerId, userName);
        for (TrainEntityV2 train : bill.getTrains()) {
            this.reply(deliverTo, train.format(this.getMyPolly().formatting()));
        }
        this.reply(deliverTo, "=========================");
        
        this.reply(deliverTo, bill.toString());
        return false;
    }
}
