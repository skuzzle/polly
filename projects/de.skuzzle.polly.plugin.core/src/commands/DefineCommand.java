package commands;

import java.io.IOException;

import core.util.WikiReader;
import polly.core.Messages;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.DelayedCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.resources.Resources;


public class DefineCommand extends DelayedCommand {

    
    public DefineCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "define", 10000);
        this.createSignature(Messages.defineSig0Desc,
            MyPlugin.DEFINE_PERMISSION,
            new Parameter(Messages.defineSig0Term, Types.STRING));
        this.setHelpText(Messages.defineHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
            throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            final String article = signature.getStringValue(0);
            final String lang = Resources.getLocale().getLanguage();
            final WikiReader wr = new WikiReader();
            
            try {
                this.reply(channel, wr.readFirstParagraph(article, lang));
                this.reply(channel, Messages.bind(Messages.defineMoreInfo, 
                        wr.getWikiLink(article, lang)));
            } catch (IOException e) {
                throw new CommandException(Messages.bind(Messages.defineError, article));
            }
        }
        return false;
    }
    
    
}
