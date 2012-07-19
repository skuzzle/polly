package polly.memes;

import polly.memes.commands.AddMemeCommand;
import polly.memes.commands.DeleteMemeCommand;
import polly.memes.commands.MemeCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;


public class MyPlugin extends PollyPlugin {

    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, 
                DuplicatedSignatureException {
        super(myPolly);
        
        myPolly.persistence().registerEntity(MemeEntity.class);
        
        MemeManager memeManager = new MemeManager(myPolly.persistence());
        this.addCommand(new MemeCommand(myPolly, memeManager));
        this.addCommand(new AddMemeCommand(myPolly, memeManager));
        this.addCommand(new DeleteMemeCommand(myPolly, memeManager));
    }

}
