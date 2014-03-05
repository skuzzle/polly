package polly.rx.httpv2;

import java.util.Date;
import java.util.AbstractList;
import java.util.List;

import polly.rx.MSG;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.model.Portal;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;


public class PortalModel extends AbstractHTMLTableModel<Portal> {

    private final static String[] COLUMNS = MSG.htmlPortalColumns.split(";"); //$NON-NLS-1$
    
    
    
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
    public Object getCellValue(int column, Portal element) {
        switch (column) {
        case 0: return element.getType().toString();
        case 1: return element.getOwnerName();
        case 2: return element.getOwnerClan();
        case 3: return element.getSector().toString();
        case 4: return element.getDate();
        default: return ""; //$NON-NLS-1$
        }
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
        case 1:
        case 2:
        case 3:
            return String.class;
        case 4:
            return Date.class;
        }
        return super.getColumnClass(column);
    }

    
    
    @Override
    public List<Portal> getData(HttpEvent e) {
        final List<? extends Portal> portals = 
                Orion.INSTANCE.getPortalProvider().getAllPortals();
        return new AbstractList<Portal>() {

            @Override
            public Portal get(int i) {
                return portals.get(i);
            }

            @Override
            public int size() {
                return portals.size();
            }
        };
    }
}
