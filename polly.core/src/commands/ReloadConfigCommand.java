package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class ReloadConfigCommand extends Command {

    public ReloadConfigCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "reloadcfg");
        this.createSignature("Liest die Konfigurationsdatei neu ein");
        this.setHelpText("Liest die Konfigurationsdatei neu ein. Nicht alle " +
        		"Konfigurationseinstellungen können zur Laufzeit übernommen werden." );
        this.setUserLevel(UserManager.ADMIN);
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) throws CommandException, InsufficientRightsException {
        
        this.getMyPolly().configuration().reload();
        
        return false;
    }

}
