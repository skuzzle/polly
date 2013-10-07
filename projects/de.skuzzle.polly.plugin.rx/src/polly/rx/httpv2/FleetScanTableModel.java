package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.FleetScan;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;

public class FleetScanTableModel extends AbstractHTMLTableModel<FleetScan> {

    private final static String[] COLUMNS = { "Date", "Location", "Sens",
            "Owner", "Clan", "Fleet Name", "Tag", "Ships" };

    protected final FleetDBManager fleetDb;



    public FleetScanTableModel(FleetDBManager fleetDb) {
        this.fleetDb = fleetDb;
        this.requirePermission(FleetDBManager.VIEW_FLEET_SCAN_PERMISSION);
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
    public String getHeader(int column) {
        return COLUMNS[column];
    }



    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }



    @Override
    public Object getCellValue(int column, FleetScan element) {
        switch (column) {
        case 0: return element.getDate();
        case 1: return element.getQuadrant() + " " + element.getX() + ", " + element.getY();
        case 2: return element.getLocalSens();
        case 3: return element.getOwnerName();
        case 4: return element.getOwnerClan();
        case 5: return new HTMLElement("a")
            .attr("href", "/pages/fleetScanDetails?scanId="+element.getId())
            .content(element.getFleetName());
        case 6: return element.getFleetTag();
        case 7: return element.getShipCount();
        default: return "";
        }
    }



    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return Date.class;
        }
        return super.getColumnClass(column);
    }

    
    
    @Override
    public int getDefaultSortColumn() {
        return 0;
    }
    
    

    @Override
    public SortOrder getDefaultSortOrder() {
        return SortOrder.DESCENDING;
    }


    @Override
    public List<FleetScan> getData(HttpEvent e) {
        return this.fleetDb.getAllScans();
    }
}
