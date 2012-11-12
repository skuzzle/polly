package polly.rx.http;

import polly.rx.MyPlugin;
import polly.rx.core.ScoreBoardManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class ScoreBoardHttpAction extends HttpAction {

    private ScoreBoardManager sbeManager;
    
    
    public ScoreBoardHttpAction(MyPolly myPolly, ScoreBoardManager sbeManager) {
        super("/sbe", myPolly);
        this.requirePermission(MyPlugin.SBE_PERMISSION);
        this.sbeManager = sbeManager;
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        
        return null;
    }

}
