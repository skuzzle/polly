package polly.rx.httpv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polly.rx.MSG;
import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleReportShip;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;


public class BattleReportShipModel extends AbstractHTMLTableModel<BattleReportShip> {

    private final static String[] COLUMNS = MSG.reportShipModelColumns.split(","); //$NON-NLS-1$
    
    private final static String REPORT_ID = "reportId"; //$NON-NLS-1$
    
    protected final FleetDBManager fleetDb;
    protected final boolean attacker;
    
    
    
    public BattleReportShipModel(FleetDBManager fleetDb, boolean attacker) {
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
            return new ShipId(element.getRxId(), fss != null);
        }
        case 1: {
            final FleetScanShip fss = this.fleetDb.fleetScanShipById(element.getRxId());
            if (fss == null) {
                return MSG.reportShipModelUnknown;
            } else {
                return fss.getOwner();
            }
        }
        case 2: return element.getShipType();
        case 3: return element.getShipClass();
        case 4: return element.getSimpleName();
        case 5: return element.getCrewXp();
        case 6: return element.getCapi();
        case 7: return element.getCapiHp();
        case 8: return element.getCapiXp();
        case 9: return element.calculateKw();
        case 10: return element.getMaxWend();
        case 11: return element.getAttack();
        case 12: return element.getShields();
        case 13: return element.getShieldDamage();
        case 14: return element.getPz();
        case 15: return element.getPzDamage();
        case 16: return element.getStructure();
        case 17: return element.getStructureDamage();
        default: return ""; //$NON-NLS-1$
        }
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0: return Object.class;
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
        case 17: return Integer.class;
        case 9: return Double.class;
        default: return super.getColumnClass(column);
        }
    }
    
    
    
    @Override
    public Map<String, String> getRequestParameters(HttpEvent e) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put(REPORT_ID, e.get(REPORT_ID));
        return result;
    }
    
    

    @Override
    public List<BattleReportShip> getData(HttpEvent e) {
        final int reportId = Integer.parseInt(e.get(REPORT_ID));
        final BattleReport br = this.fleetDb.getReportById(reportId);
        if (this.attacker) {
            return br.getAttackerShips();
        } else {
            return br.getDefenderShips();
        }
    }
    
    
    
    @Override
    public String getRefreshKey() {
        return REPORT_ID;
    }
}
