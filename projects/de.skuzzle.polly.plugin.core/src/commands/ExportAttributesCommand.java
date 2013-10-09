package commands;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polly.core.Messages;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class ExportAttributesCommand extends Command {

    public ExportAttributesCommand(MyPolly polly) 
                throws DuplicatedSignatureException {
        super(polly, "expattr"); //$NON-NLS-1$
        this.createSignature(Messages.expAttributesSig0Desc, 
            MyPlugin.EXPORT_ATTRIBUTES_PERMISSION);
        this.createSignature(Messages.expAttributesSig1Desc,
            MyPlugin.EXPORT_USER_ATTRIBUTES_PERMISSION,
            new Parameter(Messages.userName, Types.USER));
        this.setHelpText(Messages.expAttributesHelp);
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
                throws CommandException, InsufficientRightsException {
        
        User user = executer;
        if (this.match(signature, 1)) {
            user = this.getMyPolly().users().getUser(signature.getStringValue(0));
            if (user == null) {
                throw new CommandException(Messages.bind(
                        Messages.unknownUser, signature.getStringValue(0)));
            }
        }
        this.reply(channel, this.export(user));
        
        return false;
    }
    
    
    
    private String export(User user) throws CommandException {
        StringBuilder b = new StringBuilder();
        List<String> names = new ArrayList<String>(user.getAttributeNames());
        Collections.sort(names);
        for (String att : names) {
            b.append(":setattr \""); //$NON-NLS-1$
            b.append(att);
            b.append("\" \""); //$NON-NLS-1$
            b.append(user.getAttribute(att));
            b.append("\"\n"); //$NON-NLS-1$
        }
        
        try {
            return this.getMyPolly().pasting().getRandomService().paste(b.toString());
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

}
