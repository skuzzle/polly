package polly.rx.http.battlereports;

import java.util.ArrayList;
import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.BattleTactic;
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
    private final static int PZ_WARNING = 2500;
    
    
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
        
        c.put("warning", PZ_WARNING);
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
                
                
                final String w = e.getProperty("warning");
                int warning = PZ_WARNING;
                if (w != null) {
                    warning = Integer.parseInt(w);
                }
                
                final String fleetName = br.getTactic() == BattleTactic.ALIEN
                    ? br.getDefenderFleetName()
                    : br.getAttackerFleetName();
                    
                final List<BattleReportShip> damaged = new ArrayList<>();
                final List<BattleReportShip> source = 
                    br.getTactic() == BattleTactic.ALIEN 
                    ? br.getDefenderShips() 
                    : br.getAttackerShips();
                    
                for (final BattleReportShip ship : source) {
                    if (ship.getCurrentPz() <= warning) {
                        damaged.add(ship);
                    }
                }
                
                c.put("fleetName", fleetName);
                c.put("damaged", damaged);
                c.put("warning", warning);
            } catch (ParseException e1) {
                e.throwTemplateException(e1);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        return c;
    }
}
