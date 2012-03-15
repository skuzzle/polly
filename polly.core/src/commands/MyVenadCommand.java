package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.ConstraintException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;



public class MyVenadCommand extends Command {

    public MyVenadCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "myvenad");
        this.createSignature("Speichert deinen Venad-Namen", 
            new Parameter("Venad-Name", new StringType()));
        this.setRegisteredOnly();
        this.setHelpText("Befehl zum Speichern deines Venad-Namens.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            String vname = signature.getStringValue(0);
            
            try {
                this.getMyPolly().users().setAttributeFor(executer, "VENAD", vname);
                this.reply(channel, "Venadname gespeichert.");
            } catch (DatabaseException e) {
                this.reply(channel, "Interner Datenbankfehler.");
            } catch (ConstraintException e) {
                this.reply(channel, "Wert konnte nicht gesetzt werden.");
            }
        }
        
        return false;
    }

}
