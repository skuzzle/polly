package commands;

import polly.core.Messages;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class LmgtfyCommand extends SearchEngineCommand {

    public LmgtfyCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "lmgtfy"); //$NON-NLS-1$
        this.createSignature(Messages.lmgtfySig0Desc, 
            new Parameter(Messages.lmgtfySig0Term, Types.STRING));
        this.setHelpText(Messages.lmgtfyHelp);
    }


    
    @Override
    protected String getSearchLink(String key) {
        return "http://www.google.de/search?q=" + key; //$NON-NLS-1$
    }
}
