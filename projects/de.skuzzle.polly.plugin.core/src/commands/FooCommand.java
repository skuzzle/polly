package commands;

import polly.core.MSG;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.FormatManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;



public class FooCommand extends Command {

    public FooCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "foo"); //$NON-NLS-1$
        this.createSignature(MSG.fooSig0Desc, 
            new Parameter(MSG.fooSigParam, Types.ANY));
        this.createSignature(MSG.fooSig1Desc, 
            new Parameter(MSG.fooSigParam, Types.ANY), 
            new Parameter(MSG.fooSigParam, Types.ANY));
        this.createSignature(MSG.fooSig2Desc, 
            new Parameter(MSG.fooSigParam, Types.ANY), 
            new Parameter(MSG.fooSigParam, Types.ANY), 
            new Parameter(MSG.fooSigParam, Types.ANY));
        this.createSignature(MSG.fooSig3Desc);
        this.setHelpText(MSG.fooHelp);
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        this.reply(channel, this.execute(signature));
        return false;
    }
    
    
    
    private String execute(Signature signature) {
        FormatManager fm = this.getMyPolly().formatting();
        if (this.match(signature, 0)) {
            Types t1 = signature.getValue(0);
            return t1.valueString(fm);
        } else if (this.match(signature, 1)) {
            Types t1 = signature.getValue(0);
            Types t2 = signature.getValue(1);
            return t1.valueString(fm) + " " + t2.valueString(fm); //$NON-NLS-1$
        } else if (this.match(signature, 2)) {
            Types t1 = signature.getValue(0);
            Types t2 = signature.getValue(1);
            Types t3 = signature.getValue(2);
            return t1.valueString(fm) + " " + t2.valueString(fm) + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                    t3.valueString(fm);
        }
        // unreachable
        return ""; //$NON-NLS-1$
    }
}
