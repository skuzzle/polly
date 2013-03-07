package commands;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class GooglePicsCommand extends SearchEngineCommand {

    public GooglePicsCommand(MyPolly myPolly) throws DuplicatedSignatureException {
        super(myPolly, "pix");
        this.createSignature("Gibt einen Link zur google Bilder Suche zurück", 
            new Parameter("Suchbegriff", Types.STRING));
    }
    

    
    @Override
    protected String getSearchLink(String key) {
        key = key.replace(" ", "+");
        return "https://www.google.com/search?q=" + key + "&tbm=isch";
    }

}
