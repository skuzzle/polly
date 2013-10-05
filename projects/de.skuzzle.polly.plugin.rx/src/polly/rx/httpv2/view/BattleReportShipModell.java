package polly.rx.httpv2.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;


public class BattleReportShipModell extends AbstractHTMLTableModel<BattleReportShip> {

    private final static String[] COLUMNS = {
        "Rx Id", "Owner", "Name", "Crew Xp", "Captain", "HP", "Capi Xp", "KW (T100)", 
        "Max Wend", "Aw", "Shield", "Shield Dmg", "Pz", "Pz Dmg", "Str", "Str Dmg"
    };
    
    protected final FleetDBManager fleetDb;
    protected final boolean attacker;
    
    
    
    public BattleReportShipModell(FleetDBManager fleetDb, boolean attacker) {
        this.fleetDb = fleetDb;
        this.attacker = attacker;
        this.requirePermission(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
    }
    
    
    
    @Override
    public String getHeader(int column) {
        return COLUMNS[column];
    }
    
    

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    
    
    @Override
    public boolean isSortable(int column) {
        return true;
    }
    
    
    
    @Override
    public boolean isFilterable(int column) {
        return true;
    }
    
    

    @Override
    public Object getCellValue(int column, BattleReportShip element) {
        switch (column) {
        case 0: { 
            final FleetScanShip fss = this.fleetDb.fleetScanShipById(element.getRxId());
            if (fss == null) {
                return element.getRxId();
            } else {
                return new HTMLElement("a").href("/pages/scanShipDetails?shipId=" + 
                        element.getRxId());
            }
        }
        case 1: {
            final FleetScanShip fss = this.fleetDb.fleetScanShipById(element.getRxId());
            if (fss == null) {
                return "Unknown";
            } else {
                return fss.getOwner();
            }
        }
        case 2: return element.getName();
        case 3: return element.getCrewXp();
        case 4: return element.getCapi();
        case 5: return element.getCapiHp();
        case 6: return element.getCapiXp();
        case 7: return element.calculateKw();
        case 8: return element.getMaxWend();
        case 9: return element.getAttack();
        case 10: return element.getShields();
        case 11: return element.getShieldDamage();
        case 12: return element.getPz();
        case 13: return element.getPz();
        case 14: return element.getStructure();
        case 15: return element.getStructureDamage();
        default: return "";
        }
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 7: return Double.class;
        default: return super.getColumnClass(column);
        }
    }
    
    
    
    @Override
    public Map<String, String> getRequestParameters(HttpEvent e) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("reportId", e.get("reportId"));
        return result;
    }
    
    

    @Override
    public List<BattleReportShip> getData(HttpEvent e) {
        final int reportId = Integer.parseInt(e.get("reportId"));
        final BattleReport br = this.fleetDb.getReportById(reportId);
        if (this.attacker) {
            return br.getAttackerShips();
        } else {
            return br.getDefenderShips();
        }
    }
}
