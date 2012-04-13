package polly.memes.commands;


import polly.memes.MemeManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.UserManager;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class DeleteMemeCommand extends Command {

    private MemeManager memeManager;
    
    
    public DeleteMemeCommand(MyPolly polly, MemeManager memeMananger) 
            throws DuplicatedSignatureException {
        super(polly, "delmeme");
        this.memeManager = memeMananger;
        this.createSignature("Löscht ein Meme", 
            new Parameter("Name", Types.STRING));
        this.setHelpText("Löscht ein Meme aus der Datenbank.");
        this.setRegisteredOnly();
        this.setUserLevel(UserManager.ADMIN);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String name = signature.getStringValue(0);
            try {
                this.memeManager.deleteMeme(name);
                this.reply(channel, "Meme gelöscht.");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }

}
