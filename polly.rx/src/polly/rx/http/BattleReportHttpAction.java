package polly.rx.http;

import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.RxRessource;
import polly.rx.parsing.BattleReportParser;
import polly.rx.parsing.ParseException;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class BattleReportHttpAction extends HttpAction {

    
    private FleetDBManager fleetDBManager;
    
    
    
    public BattleReportHttpAction(MyPolly myPolly, FleetDBManager fleetDBManager) {
        super("/Kampfberichte", myPolly);
        this.fleetDBManager = fleetDBManager;
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e)  throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext("pages/battlereports.html");
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("postReport")) {
            String report = e.getProperty("paste");
            
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
        } else if (action != null && action.equals("delete")) {
            int id = Integer.parseInt(e.getProperty("id"));
            
            try {
                this.fleetDBManager.deleteReportById(id);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        
        BattleDrop[] dropSum = new BattleDrop[14];
        List<BattleReport> allReports = this.fleetDBManager.getAllReports();
        
        for (BattleReport report : allReports) {
            for (int i = 0; i < 14; ++i) {
                if (dropSum[i] == null) {
                    dropSum[i] = new BattleDrop(RxRessource.byOrdinal(i), 
                        report.getDrop().get(i).getAmount());
                } else {
                    dropSum[i].incAmout(report.getDrop().get(i).getAmount());
                }
            }
        }
        
        c.put("dropSum", dropSum);
        c.put("allReports", allReports);
        
        return c;
    }

}
