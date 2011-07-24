package commands;

import java.util.Iterator;
import java.util.Set;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;

public class VarCommand extends Command {

    public VarCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "var");
        this.createSignature("Zeigt die Deklarationen des ausführenden Benutzers an");
        this.setHelpText("Listet die verfügbaren Variablen für einen Benutzer auf.");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            StringBuilder b = new StringBuilder();
            Set<String> d = this.getMyPolly().users().getDeclaredIdentifiers(executer);
            b.append("Deklarationen: ");
            Iterator<String> it = d.iterator();
            while (it.hasNext()) {
                b.append(it.next());
                if (it.hasNext()) {
                    b.append(", ");
                }
            }
            this.reply(channel, b.toString());
        }
        return false;
    }

}
