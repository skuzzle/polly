package commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import polly.core.MSG;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class AmazonCommand extends SearchEngineCommand {

    public AmazonCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "amazon"); //$NON-NLS-1$
        this.createSignature(MSG.amazoneSig0Desc, 
                new Parameter(MSG.amazonSig0Term, Types.STRING));
        this.setHelpText(MSG.amazonHelp);
    }
    
    
    

    @Override
    protected String getSearchLink(String key) {
        try {
            return "www.amazon.de/s/field-keywords="+ URLEncoder.encode(key, "UTF-8");  //$NON-NLS-1$//$NON-NLS-2$
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "www.amazon.de/s/field-keywords=" + key; //$NON-NLS-1$
        }
    }
}
