package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.MSG;
import polly.rx.core.orion.QuadrantProvider;
import polly.rx.core.orion.http.OrionController;
import polly.rx.core.orion.model.Sector;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;

public class SectorTableModel extends AbstractHTMLTableModel<Sector> {

    private static final String[] HEADERS = MSG.htmlSectorsColumns.split(";"); //$NON-NLS-1$

    private final QuadrantProvider quadProvider;

    public SectorTableModel(QuadrantProvider quadProvider) {
        this.quadProvider = quadProvider;
        requirePermission(OrionController.SEARCH_SECTORS_PERMISSION);
    }

    @Override
    public String getHeader(int column) {
        return HEADERS[column];
    }

    @Override
    public int getColumnCount() {
        return HEADERS.length;
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
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return Date.class;
        }
        return super.getColumnClass(column);
    }

    @Override
    public Object getCellValue(int column, Sector element) {
        // Quadrant;X;Y;Angreifer;Verteidiger;Sektor Wache;Typ
        switch (column) {
        case 0:
            return element.getDate();
        case 1:
            return element.getQuadName();
        case 2:
            return element.getX();
        case 3:
            return element.getY();
        case 4:
            return element.getAttackerBonus();
        case 5:
            return element.getDefenderBonus();
        case 6:
            return element.getSectorGuardBonus();
        case 7:
            return element.getType();
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public List<Sector> getData(HttpEvent e) {
        return this.quadProvider.getAllSectors();
    }

}
