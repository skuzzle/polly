package polly.rx.http;

import java.util.Collection;

import polly.rx.MyPlugin;
import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.parsing.ScoreBoardParser;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;


public class ScoreBoardHttpAction extends HttpAction {

    private ScoreBoardManager sbeManager;
    
    
    public ScoreBoardHttpAction(MyPolly myPolly, ScoreBoardManager sbeManager) {
        super("/Scoreboard", myPolly);
        this.requirePermission(MyPlugin.SBE_PERMISSION);
        this.sbeManager = sbeManager;
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        HttpTemplateContext c = new HttpTemplateContext("pages/sbe.html");
        
        String action = e.getProperty("action");
        if (action != null && action.equals("postSB")) {
            String paste = e.getProperty("paste");
            try {
                Collection<ScoreBoardEntry> ents = ScoreBoardParser.parse(paste);
                
                for (ScoreBoardEntry ent : ents) {
                    this.sbeManager.addEntry(ent);
                }
            } catch (Exception e1) {
                e.throwTemplateException("Invalid paste", "");
            }
        } else if (action != null && action.equals("delete")) {
            String idS = e.getProperty("id");
            if (idS != null) {
                int id = Integer.parseInt(idS);
                try {
                    this.sbeManager.deleteEntry(id);
                } catch (DatabaseException e1) {
                    e.throwTemplateException(e1);
                }
            }
        }
        
        Collection<ScoreBoardEntry> entries = this.sbeManager.getEntries();
        c.put("entries", ScoreBoardEntry.postFilter(entries));
        
        HttpTemplateSortHelper.makeListSortable(
            c, e, "sortKey", "dir", "getRank");
        
        return c;
    }

}
