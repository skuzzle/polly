package commands;

import polly.core.Messages;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class GooglePicsCommand extends SearchEngineCommand {

    public GooglePicsCommand(MyPolly myPolly) throws DuplicatedSignatureException {
        super(myPolly, "pix");
        this.createSignature(Messages.pixSig0Desc, 
            new Parameter(Messages.pixSig0Term, Types.STRING));
        this.setHelpText(Messages.pixHelp);
    }
    

    
    @Override
    protected String getSearchLink(String key) {
        key = key.replace(" ", "+");
        return "https://www.google.com/search?q=" + key + "&tbm=isch";
    }
}
