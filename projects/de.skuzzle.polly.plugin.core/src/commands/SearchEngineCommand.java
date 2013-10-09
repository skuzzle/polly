package commands;

import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.User;

public abstract class SearchEngineCommand extends Command {

    public SearchEngineCommand(MyPolly polly, String commandName) {
        super(polly, commandName);
    }

    
    protected abstract String getSearchLink(String key);
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        
        if (this.match(signature, 0)) {
            String article = signature.getStringValue(0);
            article = article.replaceAll(" ", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
            this.reply(channel, this.getSearchLink(article));
        }
        return false;
    }
}
