package commands;

import java.util.Iterator;
import java.util.Set;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class ListAttributesCommand extends Command {

    public ListAttributesCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "listattr"); //$NON-NLS-1$
        this.createSignature(MSG.listAttributesSig0Desc.s, 
                MyPlugin.LIST_ATTRIBUTES_PERMISSION);
        this.setHelpText(MSG.listAttributesHelp.s);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        Set<String> names = executer.getAttributeNames();
        Iterator<String> it = names.iterator();
        StringBuilder result = new StringBuilder();
        while (it.hasNext()) {
            String name = it.next();
            result.append(name);
            if (it.hasNext()) {
                result.append(", "); //$NON-NLS-1$
            }
        }
        
        this.reply(channel, result.toString());
        return false;
    }

}
