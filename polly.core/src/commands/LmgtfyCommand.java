package commands;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class LmgtfyCommand extends SearchEngineCommand {

    public LmgtfyCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "lmgtfy");
        this.createSignature("Gibt einen Google-Link zurück.", 
            new Parameter("Suchbegriff", new StringType()));
    }

    
    
    @Override
    protected String getSearchLink(String key) {
        return "http://www.google.de/search?q=" + key;
    }
}
