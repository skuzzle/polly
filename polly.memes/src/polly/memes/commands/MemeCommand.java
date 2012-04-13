package polly.memes.commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;
import polly.memes.MemeEntity;
import polly.memes.MemeManager;


public class MemeCommand extends Command {

    private MemeManager memeManager;
    
    
    public MemeCommand(MyPolly polly, MemeManager memeMananger) 
            throws DuplicatedSignatureException {
        super(polly, "meme");
        this.memeManager = memeMananger;
        this.createSignature("Ruft den Link zu einem Meme ab", 
            new Parameter("Name", Types.STRING));
        this.setHelpText("Ruft Links zu bekannten Internetmemes ab");
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            MemeEntity meme = this.memeManager.getMeme(signature.getStringValue(0));
            this.reply(channel, meme.getUrl());
        }
        return false;
    }
}
