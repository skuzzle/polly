package polly.rx.http;

import java.util.Collection;

import polly.rx.MyPlugin;
import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;
import de.skuzzle.polly.sdk.time.Milliseconds;


public class ScoreboardDetailsHttpAction extends HttpAction {

    private ScoreBoardManager sbeManager;
    


    public ScoreboardDetailsHttpAction(MyPolly myPolly, ScoreBoardManager sbeManager) {
        super("/sbe_details", myPolly);
        this.sbeManager = sbeManager;
        this.requirePermission(MyPlugin.SBE_PERMISSION);
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        HttpTemplateContext c = new HttpTemplateContext("pages/sbe_details.html");
        
        String venadName = e.getProperty("venad");
        
        Collection<ScoreBoardEntry> entries = this.sbeManager.getEntries(venadName);
        
        ScoreBoardEntry oldest = entries.iterator().next();
        ScoreBoardEntry youngest = entries.iterator().next();
        
        for (ScoreBoardEntry entry : entries) {
            if (entry.getDate().getTime() < oldest.getDate().getTime()) {
                oldest = entry;
            }
            if (entry.getDate().getTime() > youngest.getDate().getTime()) {
                youngest = entry;
            }
        }
        
        long diff = Math.abs(youngest.getDate().getTime() - oldest.getDate().getTime());
        long days = Milliseconds.toDays(diff);
        int pointDiff = youngest.getPoints() - oldest.getPoints();
        double pointsPerDay = (double) pointDiff / (double)days;
        
        HttpTemplateSortHelper.makeListSortable(
            c, e, "sortKey", "dir", "getRank");
        
        c.put("entries", entries);
        c.put("venad", e.getSource().escapeHtml(venadName));
        c.put("span", diff);
        c.put("days", days);
        c.put("pointsPerDay", pointsPerDay);
        c.put("nformat", ScoreBoardManager.NUMBER_FORMAT);
        return c;
    }

}
