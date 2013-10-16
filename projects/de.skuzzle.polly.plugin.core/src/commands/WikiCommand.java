package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class WikiCommand extends SearchEngineCommand {

    public WikiCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "wiki"); //$NON-NLS-1$
        this.createSignature(MSG.wikiSig0Desc.s, 
            new Parameter(MSG.wikiSig0Term.s, Types.STRING));
        this.setHelpText(MSG.wikiHelp.s);
    }

    
    
    @Override
    protected String getSearchLink(String key) {
        return "http://de.wikipedia.org/wiki/" + key; //$NON-NLS-1$
    }
}
