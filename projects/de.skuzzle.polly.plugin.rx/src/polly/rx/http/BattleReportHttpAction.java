package polly.rx.http;

import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;

import de.skuzzle.polly.sdk.MyPolly;
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
        List<BattleReport> allReports = this.fleetDBManager.getAllReports();
        TemplateContextHelper.prepareForReportsList(c, e.getSession(), allReports);
        HttpTemplateSortHelper.makeListSortable(c, e, "sortKey", "dir", "getDate");
        
        c.put("fleetDBManager", this.fleetDBManager);
        return c;
    }

}
