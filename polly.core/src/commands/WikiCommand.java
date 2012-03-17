package commands;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class WikiCommand extends SearchEngineCommand {

    public WikiCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "wiki");
        this.createSignature("Gibt einen Link zum angegebenen Wikipedia-Artikel zurück", 
            new Parameter("Suchbegriff", Types.newString()));
        
        this.setHelpText("Befehl um Wikipedia-Artikel abzurufen.");
    }

    
    
    @Override
    protected String getSearchLink(String key) {
        return "http://de.wikipedia.org/wiki/" + key;
    }
}
