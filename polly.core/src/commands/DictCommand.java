package commands;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class DictCommand extends SearchEngineCommand {

    public DictCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "dict");
        this.createSignature("Gibt einen Dict.cc-Link zurück.", 
            new Parameter("Satz", Types.STRING));
    }

    
    
    @Override
    protected String getSearchLink(String key) {
        return "http://www.dict.cc/?s=" + key;
    }
}
