package polly.rx.http.battlereports;

import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.http.TemplateContextHelper;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.HttpTemplateSortHelper;


public class BattleReportHttpAction extends FilteredBattleReportView {

    
    
    public BattleReportHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/Kampfberichte", myPolly,  fleetDBManager);
        this.requirePermission(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
    }



    @Override
    protected HttpTemplateContext createContext(HttpEvent e)
            throws HttpTemplateException, InsufficientRightsException {
        
        final HttpTemplateContext c = new HttpTemplateContext("pages/battlereports.html");
        c.put("fleetDBManager", this.fleetDBManager);
        return c;
    }
    
    
    
    @Override
    protected void postProcess(List<BattleReport> sublist, HttpTemplateContext c, 
        HttpEvent e) throws HttpTemplateException, InsufficientRightsException {
        
        TemplateContextHelper.prepareForReportsList(c, e.getSession(), sublist);
        HttpTemplateSortHelper.makeListSortable(c, e, "sortKey", "dir", "getDate");
    }

}
