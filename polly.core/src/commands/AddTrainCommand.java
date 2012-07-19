package commands;

import polly.core.MyPlugin;
import core.TrainBill;
import core.TrainManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.TrainEntity;


public class AddTrainCommand extends Command {
    
    private TrainManager trainManager;

    public AddTrainCommand(MyPolly polly, TrainManager trainManager) 
            throws DuplicatedSignatureException {
        
        super(polly, "train");
        this.createSignature("Fügt ein neues Training zur Rechnung des Benutzers hinzu.", 
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER),
            new Parameter("Rechnung", Types.STRING));
        this.createSignature("Zeigt die offene Rechnungssumme für einen Benutzer an.",
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER),
            new Parameter("Details?", Types.BOOLEAN));
        this.createSignature("Fügt ein neues Training zur Rechnung des Benutzers hinzu.",
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER),
            new Parameter("Rechnung", Types.STRING),
            new Parameter("Faktor", Types.NUMBER));
        this.createSignature("Zeigt die offene Rechnungssumme für einen Benutzer an.",
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER));
        this.setHelpText("Befehl zum Verwalten von Capi Trainings.");
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {

        double mod = 1.0;
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            this.addTrain(channel, userName, train, mod);
        } else if (this.match(signature, 2)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            mod = signature.getNumberValue(2);
            this.addTrain(channel, userName, train, mod);
        } else if (this.match(signature, 1)) {
            String userName = signature.getStringValue(0);
            boolean showAll = signature.getBooleanValue(1);
            
            TrainBill bill = this.trainManager.getBill(userName);
            this.outputTrain(showAll, bill, channel);
        } else if (this.match(signature, 3)) {
            String userName = signature.getStringValue(0);
            TrainBill bill = this.trainManager.getBill(userName);

            this.outputTrain(false, bill, channel);
        }
        return false;
    }
    
    
    
    private void outputTrain(boolean detailed, TrainBill bill, String channel) {
        if (detailed) {
            for (TrainEntity train : bill.getTrains()) {
                this.reply(channel, train.format(this.getMyPolly().formatting()));
            }
            this.reply(channel, "=========================");
        }
        
        this.reply(channel, bill.toString());
    }
    
    
    private void addTrain(String channel, String userName, String train, double mod) {
        try {
            TrainEntity e = TrainEntity.forString(userName, train, mod);
            this.trainManager.addTrain(e);
            this.reply(channel, "Posten gespeichert.");
        } catch (Exception e) {
            this.reply(channel, "Fehler beim Speichern.");
        }
    }
}
