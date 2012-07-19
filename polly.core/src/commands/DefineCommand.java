package commands;

import java.io.IOException;

import core.util.WikiReader;

import polly.core.MyPlugin;

import de.skuzzle.polly.sdk.DelayedCommand;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.model.User;


public class DefineCommand extends DelayedCommand {

    
    public DefineCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "define", 10000);
        this.createSignature("Sucht nach der Definition des angegebenen Begriffs",
            MyPlugin.DEFINE_PERMISSION,
            new Parameter("Begriff", Types.STRING));
        this.setHelpText("Sucht nach der Definition eines angegebenen Begriffs bei " +
    		"Wikipedia");
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel, Signature signature) 
        throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            String article = signature.getStringValue(0);
            String lang = "de";
            WikiReader wr = new WikiReader();
            
            try {
                this.reply(channel, wr.readFirstParagraph(article, lang));
                this.reply(channel, "Mehr Infos: " + wr.getWikiLink(article, lang));
            } catch (IOException e) {
                throw new CommandException("Fehler beim Zugriff auf den Wikipedia " +
                		"Artikel '" + article + "'");
            }
        }
        return false;
    }
    
    
}
