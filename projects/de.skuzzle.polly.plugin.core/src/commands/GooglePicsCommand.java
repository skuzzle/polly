package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class GooglePicsCommand extends SearchEngineCommand {

    public GooglePicsCommand(MyPolly myPolly) throws DuplicatedSignatureException {
        super(myPolly, "pix"); //$NON-NLS-1$
        this.createSignature(MSG.pixSig0Desc.s, 
            new Parameter(MSG.pixSig0Term.s, Types.STRING));
        this.setHelpText(MSG.pixHelp.s);
    }
    

    
    @Override
    protected String getSearchLink(String key) {
        key = key.replace(" ", "+"); //$NON-NLS-1$ //$NON-NLS-2$
        return "https://www.google.com/search?q=" + key + "&tbm=isch"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
