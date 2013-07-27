package polly.rx.http;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.parsing.BattleReportParser;
import polly.rx.parsing.ParseException;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class AddBattleReportAction extends HttpAction {

    private final FleetDBManager fleetDBManager;
    
    
    public AddBattleReportAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/add_report", myPolly);
        this.fleetDBManager = fleetDBManager;
        this.requirePermission(FleetDBManager.ADD_BATTLE_REPORT_PERMISSION);
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) throws HttpTemplateException,
            InsufficientRightsException {
        final HttpTemplateContext c = new HttpTemplateContext("pages/add_report.html");
        final String action = e.getProperty("action");
        
        if (action != null && action.equals("postReport")) {
            if (!this.getMyPolly().roles().hasPermission(e.getSession().getUser(), 
                    FleetDBManager.ADD_BATTLE_REPORT_PERMISSION)) {
                
                throw new InsufficientRightsException(this);
            }
        
            final String report = e.getSource().escapeHtml(e.getProperty("paste"));
            try {
                BattleReport br = BattleReportParser.parseReport(report, 
                    e.getSession().getUser());
                
                this.fleetDBManager.addBattleReport(br);
            } catch (ParseException e1) {
                e.throwTemplateException(e1);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        return c;
    }
}
