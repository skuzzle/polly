package polly.rx.commands;

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


public class MyTrainsCommand extends Command {
    
    private TrainManagerV2 trainManager;


    public MyTrainsCommand(MyPolly polly, TrainManagerV2 trainManager) 
            throws DuplicatedSignatureException {
        super(polly, "mytrains");
        this.createSignature("Listet die offene Capitrain Rechnung für einen " +
        		"Benutzer auf.", MyPlugin.MYTRAINS_PERMISSION,
        		new Parameter("Trainer-Name", Types.USER));
        this.createSignature("Listet eine detaillierte Capitrainrechnung für einen " +
        		"Benutzer auf", 
        		MyPlugin.MYTRAINS_PERMISSION,
    		new Parameter("Trainer-Name", Types.USER),
    		new Parameter("Details", Types.BOOLEAN));
        this.setHelpText("Listet die offene Capitrain Rechnung für einen " +
                "Benutzer auf.");
        this.trainManager = trainManager;
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {
        
        if (this.match(signature, 0)) {
            this.printTrains(false, signature.getStringValue(0), 
                executer.getCurrentNickName(), channel);
        } else if (this.match(signature, 1)) {
            this.printTrains(signature.getBooleanValue(1), signature.getStringValue(0), 
                executer.getCurrentNickName(), channel);
        }

        return false;
    }
    
    
    
    private void printTrains(boolean detailed, String trainerNick, String forUser, 
                String channel) {
        User trainer = this.trainManager.getTrainer(trainerNick);
        TrainBillV2 b = this.trainManager.getBill(trainer, forUser);        
        if (detailed) {
            channel = forUser; // print detailed bill in query
            for (TrainEntityV3 train : b.getTrains()) {
                this.reply(channel, train.format(this.getMyPolly().formatting()));
            }
            this.reply(channel, "=========================");
        }
        this.reply(channel, b.toString());
    }
}
