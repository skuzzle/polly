package commands;

import java.util.Iterator;
import java.util.Set;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class VarCommand extends Command {

    public VarCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "var");
        this.createSignature("Zeigt die Deklarationen des ausführenden Benutzers an");
        this.createSignature("Zeigt die Deklarationen des angegebenen Namespaces an.", 
            new Parameter("Namespace", Types.newString()));
        this.createSignature("Zeigt die Deklarationen des angegebenen Benutzers an.", 
            new Parameter("User", Types.newUser()));
        this.setHelpText("Listet die verfügbaren Variablen für einen Benutzer auf.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        String ns = executer.getName();
        if (this.match(signature, 1) || this.match(signature, 2)) {
            ns = signature.getStringValue(0);
        }
        StringBuilder b = new StringBuilder();
        Set<String> d = this.getMyPolly().users().getDeclaredIdentifiers(ns);
        b.append("Deklarationen: ");
        Iterator<String> it = d.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append(", ");
            }
        }
        this.reply(channel, b.toString());
        return false;
    }

}
