package commands;

import core.TrainBill;
import core.TrainManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.Types.UserType;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.Types.BooleanType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;
import entities.TrainEntity;


public class AddTrainCommand extends Command {
    
    private TrainManager trainManager;

    public AddTrainCommand(MyPolly polly, TrainManager trainManager) 
            throws DuplicatedSignatureException {
        
        super(polly, "train");
        this.createSignature("Fügt ein neues Training zur Rechnung des Benutzers hinzu.", 
            new UserType(), new StringType());
        this.createSignature("Zeigt die offene Rechnungssumme für einen Benutzer an.", 
            new UserType(), new BooleanType());
        this.setHelpText("Befehl zum Verwalten von Capi Trainings.");
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {

        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            try {
                TrainEntity e = TrainEntity.forString(userName, train);
                this.trainManager.addTrain(e);
                this.reply(channel, "Posten gespeichert.");
            } catch (Exception e) {
                this.reply(channel, "Fehler beim Speichern.");
            }
        } else if (this.match(signature, 1)) {
            String userName = signature.getStringValue(0);
            boolean showAll = signature.getBooleanValue(1);
            
            TrainBill bill = this.trainManager.getBill(userName);
            if (showAll) {
                for (TrainEntity train : bill.getTrains()) {
                    this.reply(channel, train.format(this.getMyPolly().formatting()));
                }
                this.reply(channel, "=========================");
            }
            
            this.reply(channel, bill.toString());
        }
        return false;
    }
}
