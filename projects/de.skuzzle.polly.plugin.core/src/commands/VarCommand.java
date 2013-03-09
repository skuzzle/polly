package commands;

import java.util.Iterator;
import java.util.Set;

import polly.core.MyPlugin;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;

public class VarCommand extends Command {

    public VarCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "var");
        this.createSignature("Zeigt die Deklarationen des ausf�hrenden Benutzers an",
            MyPlugin.LIST_VARS_PERMISSION);
        this.createSignature("Zeigt die Deklarationen des angegebenen Namespaces an.",
            MyPlugin.LIST_VARS_PERMISSION,
            new Parameter("Namespace", Types.STRING));
        this.createSignature("Zeigt die Deklarationen des angegebenen Benutzers an.",
            MyPlugin.LIST_VARS_PERMISSION,
            new Parameter("User", Types.USER));
        this.setHelpText("Listet die verf�gbaren Variablen f�r einen Benutzer auf.");
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