package polly.rx.http;

import polly.rx.BattleReportManager;
import polly.rx.ParseException;
import polly.rx.entities.BattleReport;
import polly.rx.parsing.BattleReportParser;

import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;


public class BattleReportHttpAction extends HttpAction {

    
    private BattleReportManager battleReportManager;
    
    
    
    public BattleReportHttpAction(MyPolly myPolly, BattleReportManager reportManager) {
        super("/Kampfberichte", myPolly);
        this.battleReportManager = reportManager;
    }

    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e)  throws HttpTemplateException {
        HttpTemplateContext c = new HttpTemplateContext("pages/battlereports.html");
        
        String action = e.getProperty("action");
        
        if (action != null && action.equals("postReport")) {
            String report = e.getProperty("paste");
            System.out.println(report);
            try {
                BattleReport br = BattleReportParser.parse(report);
                
                this.getMyPolly().persistence().atomicPersist(br);
            } catch (ParseException e1) {
                e.throwTemplateException(e1);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
            
        }
        
        c.put("allReports", this.battleReportManager.getAllReports());
        
        return c;
    }

}
