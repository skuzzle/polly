package commands;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class ExportAttributesCommand extends Command {

    public ExportAttributesCommand(MyPolly polly) 
                throws DuplicatedSignatureException {
        super(polly, "expattr");
        this.createSignature("Exportiert alle deine polly Attribute");
        this.createSignature("Exportiert alle polly Attribute des angegebenen Benutzers", 
            new Parameter("Benutzer", Types.USER));
        this.setHelpText("Mit diesem Befehl können die individuellen polly Attributes " +
        		"eines Benutzers exportiert werden.");
        this.setRegisteredOnly();
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
                throws CommandException, InsufficientRightsException {
        
        User user = executer;
        if (this.match(signature, 1)) {
            user = this.getMyPolly().users().getUser(signature.getStringValue(0));
            if (user == null) {
                throw new CommandException("Benutzer '" + signature.getStringValue(0) + 
                    "' existiert nicht");
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
            b.append(":setattr \"");
            b.append(att);
            b.append("\" \"");
            b.append(user.getAttribute(att));
            b.append("\"\n");
        }
        
        try {
            return this.getMyPolly().pasting().getRandomService().paste(b.toString());
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

}
