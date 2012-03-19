package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class DelVarCommand extends Command {

    public DelVarCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "delvar");
        this.createSignature("Löscht die Variable mit angegebenen Namen. " +
            "Das Löschen vordefinierter Bezeichner hat keinen Effekt.", 
            new Parameter("Variablenname", Types.STRING));
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        if (this.match(signature, 0)) {
            String var = signature.getStringValue(0);
            this.getMyPolly().users().deleteDeclaration(executer, var);
            this.reply(channel, "Variable '" + var + "' wurde gelöscht.");
        }
        return false;
    }
}
