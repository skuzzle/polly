package commands;

import java.util.Iterator;
import java.util.Set;

import polly.core.MSG;
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
        super(polly, "var"); //$NON-NLS-1$
        this.createSignature(MSG.varSig0Desc.s,  MyPlugin.LIST_VARS_PERMISSION);
        this.createSignature(MSG.varSig1Desc.s,
            MyPlugin.LIST_VARS_PERMISSION,
            new Parameter(MSG.varSig1Namespace.s, Types.STRING));
        this.createSignature(MSG.varSig2Desc.s,
            MyPlugin.LIST_VARS_PERMISSION,
            new Parameter(MSG.varSig2User.s, Types.USER));
        this.setHelpText(MSG.varHelp.s);
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
        b.append(MSG.varDeclarations);
        Iterator<String> it = d.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append(", "); //$NON-NLS-1$
            }
        }
        this.reply(channel, b.toString());
        return false;
    }

}
