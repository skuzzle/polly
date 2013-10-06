package polly.rx.httpv2;

import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScanShip;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;

public class FleetScanShipTableModel extends AbstractHTMLTableModel<FleetScanShip> {

    private final static String COLUMNS[] = {
        "RX ID", "Type", "Class", "Name", "Techlevel", "Owner", "Changed Owner?", "Clan", "History"
    };

    
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
        final String href= "/pages/scanShipDetails?shipId=" + element.getRxId();
        switch (column) {
        case 0: return new HTMLElement("a").attr("href", href).content("" + element.getRxId());
        case 1: return element.getShipType();
        case 2: return element.getShipClass();
        case 3: return element.getSimpleName();
        case 4: return element.getTechlevel();
        case 5: return element.getOwner();
        case 6: return element.changedOwner();
        case 7: return element.getOwnerClan();
        case 8: return element.getHistorySize();
        default: return "";
        }
    }


    
    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 6) {
            return Boolean.class;
        }
        return super.getColumnClass(column);
    }
    
    

    @Override
    public List<FleetScanShip> getData(HttpEvent e) {
        return this.fleetDB.getAllScannedShips();
    }
}
