package polly.rx.http;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import polly.rx.MyPlugin;
import polly.rx.core.ScoreBoardManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class ScoreBoardCompareHttpAction extends HttpAction {

    private final static AtomicInteger ID_GENERATOR = new AtomicInteger();
    
    private final ScoreBoardManager sbeManager;
    
    public ScoreBoardCompareHttpAction(MyPolly myPolly, ScoreBoardManager sbeManager) {
        super("/sbe_compare", myPolly);
        this.sbeManager = sbeManager;
        this.requirePermission(MyPlugin.SBE_PERMISSION);
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        
        HttpTemplateContext c = new HttpTemplateContext("pages/sbe_compare.html");
        
        final String action = e.getProperty("action");
        if (action != null && action.equals("setMM")) {
            final String mm = e.getProperty("maxMonths");
            e.getSession().putDtata("maxMonths", mm);
        }
        
        
        final String names = e.getProperty("names");
        if (names == null || names.equals("")) {
            e.throwTemplateException("Invalid request", "No venad names to compare");
        }
        final String[] venads = names.split(";");
        
        final String mm = (String) e.getSession().get("maxMonths");
        int maxMonths;
        if (mm != null) {
            maxMonths = Integer.parseInt(mm);
        } else {
            maxMonths = Integer.parseInt(e.getSession().getUser().getAttribute(
                MyPlugin.MAX_MONTHS));
        }
        maxMonths = Math.max(Math.min(maxMonths, 24), 4);
        
        final InputStream graph = this.sbeManager.createMultiGraph(maxMonths, venads);
        final String memFileName = "compare" + ID_GENERATOR.getAndIncrement();
        e.getSource().putMemoryFile(memFileName, graph);
        c.put("names", names);
        c.put("fileName", memFileName);
        
        return c;
    }

}
