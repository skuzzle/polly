package polly.rx.httpv2;

import java.util.List;

import polly.rx.MSG;
import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;

public class FleetScanShipTableModel extends AbstractHTMLTableModel<FleetScanShip> {
    
    
    

    private final static String COLUMNS[] = MSG.scanShipModelColumns.split(","); //$NON-NLS-1$

    
    protected final FleetDBManager fleetDB;



    public FleetScanShipTableModel(FleetDBManager fleetDB) {
        this.fleetDB = fleetDB;
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
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
    public boolean isFilterable(int column) {
        return true;
    }
    
    
    
    @Override
    public boolean isSortable(int column) {
        return true;
    }

    
    
    @Override
    public Object getCellValue(int column, FleetScanShip element) {
        switch (column) {
        case 0: return new ShipId(element.getRxId());
        case 1: return element.getShipType();
        case 2: return element.getShipClass();
        case 3: return element.getSimpleName();
        case 4: return element.getTechlevel();
        case 5: return element.getOwner();
        case 6: return element.changedOwner();
        case 7: return element.getOwnerClan();
        case 8: return element.getHistorySize();
        default: return ""; //$NON-NLS-1$
        }
    }


    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0: return Number.class;
        case 2: return Integer.class;
        case 4: return Integer.class;
        case 6: return Boolean.class;
        case 8: return Integer.class;
        default: return super.getColumnClass(column);
        }
    }
    
    

    @Override
    public List<FleetScanShip> getData(HttpEvent e) {
        return this.fleetDB.getAllScannedShips();
    }
}
