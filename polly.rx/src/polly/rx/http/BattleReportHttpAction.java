package polly.rx.http;

import java.util.List;

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
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;


public class BattleReportHttpAction extends HttpAction {

    
    private FleetDBManager fleetDBManager;
    
    
    
    public BattleReportHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/Kampfberichte", myPolly);
        this.fleetDBManager = fleetDBManager;
        this.requirePermission(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e)  throws HttpTemplateException, 
            InsufficientRightsException {
        
        HttpTemplateContext c = new HttpTemplateContext("pages/battlereports.html");
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("postReport")) {
            
            if (!this.getMyPolly().roles().hasPermission(e.getSession().getUser(), 
                    FleetDBManager.ADD_BATTLE_REPORT_PERMISSION)) {
                throw new InsufficientRightsException(this);
            }
            
            String report = e.getSource().escapeHtml(e.getProperty("paste"));
            
            System.out.println(report);
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
        
        List<BattleReport> allReports = this.fleetDBManager.getAllReports();
        TemplateContextHelper.prepareForReportsList(c, e.getSession(), allReports);
        HttpTemplateSortHelper.makeListSortable(c, e, "sortKey", "dir", "getDate");
        
        return c;
    }

}
