package commands;

import java.io.IOException;

import core.util.WikiReader;
import polly.core.MSG;
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
        super(polly, "define", 10000); //$NON-NLS-1$
        this.createSignature(MSG.defineSig0Desc.s,
            MyPlugin.DEFINE_PERMISSION,
            new Parameter(MSG.defineSig0Term.s, Types.STRING));
        this.setHelpText(MSG.defineHelp.s);
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
                this.reply(channel, MSG.defineMoreInfo.s( 
                        wr.getWikiLink(article, lang)));
            } catch (IOException e) {
                throw new CommandException(MSG.defineError.s(article));
            }
        }
        return false;
    }
}
