package polly.rx.commands;

import polly.rx.core.TrainBillV2;
import polly.rx.core.TrainManagerV2;
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


public class AddTrainCommand extends Command {
    
    private TrainManagerV2 trainManager;

    public AddTrainCommand(MyPolly polly, TrainManagerV2 trainManager) 
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
            this.reply(channel, "=========================");
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
                String command = ":remind \"Training für " + forUser +
                        " abgeschlossen. Bisherige Kosten: " + bill.weightedSum() + " Cr.\" " + 
                        (te.getDuration() / 1000) + "s";
                this.getMyPolly().commands().executeString(
                        command, 
                        trainer.getCurrentNickName(), 
                        true, trainer, this.getMyPolly().irc());
            }
            this.reply(channel, "Posten gespeichert. Aktuelle Kosten: " + 
                bill.weightedSum() + " Cr.");
        } catch (Exception e) {
            throw new CommandException("Fehler beim Speichern");
        }
    }
}
