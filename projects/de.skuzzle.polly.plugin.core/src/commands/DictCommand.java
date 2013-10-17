package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class DictCommand extends SearchEngineCommand {

    public DictCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "dict"); //$NON-NLS-1$
        this.createSignature(MSG.dictSig0Desc, 
            MyPlugin.DICT_PERMISSION,
            new Parameter(MSG.dictSig0Term, Types.STRING));
        this.setHelpText(MSG.dictHelp);
    }

    
    
    @Override
    protected String getSearchLink(String key) {
        return "http://www.dict.cc/?s=" + key; //$NON-NLS-1$
    }
}
