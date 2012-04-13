package polly.memes.commands;

import java.net.MalformedURLException;
import java.net.URL;

import polly.memes.MemeEntity;
import polly.memes.MemeManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class AddMemeCommand extends Command {

    private MemeManager memeManager;
    
    
    public AddMemeCommand(MyPolly polly, MemeManager memeMananger) 
            throws DuplicatedSignatureException {
        super(polly, "addmeme");
        this.memeManager = memeMananger;
        this.createSignature("Fügt ein neues Meme hinzu", 
            new Parameter("Name", Types.STRING),
            new Parameter("URL", Types.STRING));
        this.setHelpText("Fügt einen Link zu einem neuen Meme hinzu");
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String name = signature.getStringValue(0);
            String url = signature.getStringValue(1);
            if (this.memeManager.checkMemeExists(name)) {
                throw new CommandException("Meme existiert bereits");
            }
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new CommandException("Ungültige URL: " + url);
            }
            try {
                this.memeManager.addMeme(new MemeEntity(name, url));
                this.reply(channel, "Meme hinzugefügt.");
            } catch (DatabaseException e) {
                throw new CommandException(e);
            }
        }
        return false;
    }

}
