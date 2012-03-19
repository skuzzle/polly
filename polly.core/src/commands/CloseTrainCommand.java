package commands;

import core.TrainManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class CloseTrainCommand extends Command {

    private TrainManager trainManager;
    
    public CloseTrainCommand(MyPolly polly, TrainManager trainManager) 
            throws DuplicatedSignatureException {
        super(polly, "closetrain");
        this.createSignature("Schliesst alle offenen Rechnungen für den angegebenen " +
            "Benutzer.", 
            new Parameter("User", Types.USER));
        this.createSignature("Schliesst einzelnen Trainposten.", 
            new Parameter("Train Id", Types.NUMBER));
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
        this.setHelpText("Schliesst offene Capitrain rechnungen.");
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {
        
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            try {
                this.trainManager.closeOpenTrains(userName);
                this.reply(channel, "Alle offenen Rechnungen für '" + userName + 
                    "'geschlossen.");
            } catch (DatabaseException e) {
                this.reply(channel, "Interner Datenbankfehler!");
            }
        } else if (this.match(signature, 1)) {
            int id = (int) signature.getNumberValue(0);
            try {
                this.trainManager.closeOpenTrain(id);
                this.reply(channel, "Posten mit der Id '" + id + " 'geschlossen.");
            } catch (DatabaseException e) {
                this.reply(channel, "Interner Datenbankfehler!");
            }
        }
        return false;
    }
}
