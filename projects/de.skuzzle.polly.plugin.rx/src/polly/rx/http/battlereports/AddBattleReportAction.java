package polly.rx.http.battlereports;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    private final static int KW_WARNING = 2500;
    
    
    
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
        
        final NumberFormat nf = new DecimalFormat("#");
        
        c.put("warning", PZ_WARNING);
        c.put("kwWarning", KW_WARNING);
        
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
                
                
                String w = e.getProperty("warning");
                int pzWarning = PZ_WARNING;
                if (w != null) {
                    pzWarning = Integer.parseInt(w);
                }
                
                w = e.getProperty("kwWarning");
                int kwWarning = KW_WARNING;
                if (w != null) {
                    kwWarning = Integer.parseInt(w);
                }
                
                
                final String fleetName;
                final List<BattleReportShip> source;
                final double ownKw;
                final double opponentKw;
                if (br.getTactic() == BattleTactic.ALIEN) {
                    fleetName = br.getDefenderFleetName();
                    source = br.getDefenderShips();
                    ownKw = br.getDefenderKw();
                    opponentKw = br.getAttackerKw();
                } else {
                    fleetName = br.getAttackerFleetName();
                    source = br.getAttackerShips();
                    opponentKw = br.getDefenderKw();
                    ownKw = br.getAttackerKw();
                }
                    
                final List<BattleReportShip> damaged = new ArrayList<>();
                final double kwDiff = ownKw - opponentKw;
                for (final BattleReportShip ship : source) {
                    if (ship.getCurrentPz() <= pzWarning) {
                        damaged.add(ship);
                    }
                }
                
                c.put("warnKw", kwDiff <= kwWarning);
                c.put("kwWarning", kwWarning);
                c.put("kwDiff", nf.format(kwDiff));
                c.put("fleetName", fleetName);
                c.put("damaged", damaged);
                c.put("warning", pzWarning);
            } catch (ParseException e1) {
                e.throwTemplateException(e1);
            } catch (DatabaseException e1) {
                e.throwTemplateException(e1);
            }
        }
        return c;
    }
}
