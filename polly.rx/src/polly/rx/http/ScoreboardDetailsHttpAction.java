package polly.rx.http;

import java.util.Collection;

import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;


public class ScoreboardDetailsHttpAction extends HttpAction {

    private ScoreBoardManager sbeManager;


    public ScoreboardDetailsHttpAction(MyPolly myPolly, ScoreBoardManager sbeManager) {
        super("/sbe_details", myPolly);
        this.sbeManager = sbeManager;
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
        
        long diff = youngest.getDate().getTime() - oldest.getDate().getTime();
        long days = diff / 1000 / 60 / 60 / 24;
        int pointDiff = youngest.getPoints() - oldest.getPoints();
        double pointsPerDay = (double) pointDiff / (double)days;
        
        HttpTemplateSortHelper.makeListSortable(
            c, e, "sortKey", "dir", "getRank");
        
        c.put("entries", entries);
        c.put("venad", e.getSource().escapeHtml(venadName));
        c.put("diff", diff);
        c.put("pointsPerDay", pointsPerDay);
        
        return c;
    }

}
