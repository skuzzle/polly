package polly.rx.http;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import polly.rx.parsing.ScoreBoardParser;
import de.skuzzle.polly.sdk.CSVExporter;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.time.Time;


public class PostScoreboardHttpAction extends HttpAction {
    
    
    private final ScoreBoardManager sbeManager;
    
    
    public PostScoreboardHttpAction(MyPolly myPolly, ScoreBoardManager sbeManager) {
        super("/postScoreboard", myPolly);
        this.sbeManager = sbeManager;
    }


    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        
        final String action = e.getProperty("action");
        if (action != null && action.equals("postSB")) {
            final String user = e.getProperty("user");
            final String pw = e.getProperty("pw");
            
            final User puser = this.getMyPolly().users().getUser(user);
            if (puser == null || !puser.checkPassword(pw)) {
                throw new InsufficientRightsException(this);
            }
            
            synchronized (this) {
                String paste = e.getProperty("paste");
                String d = e.getProperty("date");
                try {
                    Date date = Time.currentTime();
                    if (d != null && !d.equals("")) {
                        date = new SimpleDateFormat(
                            "EE MMM dd, yyyy hh:mm a").parse(d);
                    }
                    Collection<ScoreBoardEntry> ents = ScoreBoardParser.parse(paste, date);
                    
                    this.sbeManager.addEntries(ents);

                    Collection<ScoreBoardEntry> allEntries = this.sbeManager.getEntries();
                    CSVExporter.exportToCSV(allEntries, 
                            new File(e.getSource().getTemplateRoot(), 
                                ScoreBoardHttpAction.CSV_FILE_NAME).toString(), ",");
                } catch (Exception e1) {
                    e.throwTemplateException("Invalid paste", "");
                }
            }
        }
        return new HttpTemplateContext("pages/postScoreBoardResult.html");
    }
}
