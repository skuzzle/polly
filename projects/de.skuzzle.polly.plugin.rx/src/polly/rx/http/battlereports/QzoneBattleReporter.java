package polly.rx.http.battlereports;

import polly.rx.MyPlugin;
import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.parsing.ParseException;
import polly.rx.parsing.QBattleReportParser;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class QzoneBattleReporter extends HttpAction {

    private final FleetDBManager fleetDb;
    
    public QzoneBattleReporter(MyPolly myPolly, FleetDBManager fleetDb) {
        super("/postQReport", myPolly);
        this.fleetDb = fleetDb;
    }
    
    

    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {

        final String action = e.getProperty("action");
        if (action != null && action.equals("postBR")) {
            final String user = e.getProperty("user");
            final String pw = e.getProperty("pw");
            
            final User puser = this.getMyPolly().users().getUser(user);
            if (puser == null || !puser.checkPassword(pw)) {
                throw new InsufficientRightsException(this);
            } else if (!this.getMyPolly().roles().hasPermission(puser, 
                    MyPlugin.SBE_PERMISSION)) {
                throw new InsufficientRightsException(this);
            }
            
            try {
                final BattleReport br = QBattleReportParser.parse(e.getProperty("report"), 
                    puser.getId());
                this.fleetDb.addBattleReport(br);
                return new HttpTemplateContext(
                    "pages/postScoreBoardResult.html");
            } catch (ParseException e1) {
                throw new HttpTemplateException(e.getSession(), e1);
            } catch (DatabaseException e1) {
                throw new HttpTemplateException(e.getSession(), e1);
            }
        }
        throw new HttpTemplateException("Invalid action", 
            "Invalid action", e.getSession());
    }

}
