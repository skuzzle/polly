package commands;

import polly.core.Messages;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class DictCommand extends SearchEngineCommand {

    public DictCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "dict");
        this.createSignature(Messages.dictSig0Desc, 
            MyPlugin.DICT_PERMISSION,
            new Parameter(Messages.dictSig0Term, Types.STRING));
        this.setHelpText(Messages.dictHelp);
    }

    
    
    @Override
    protected String getSearchLink(String key) {
        return "http://www.dict.cc/?s=" + key;
    }
}
