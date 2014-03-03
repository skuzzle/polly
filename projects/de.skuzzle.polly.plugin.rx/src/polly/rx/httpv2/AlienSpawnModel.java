package polly.rx.httpv2;

import java.util.AbstractList;
import java.util.List;

import polly.rx.MSG;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.model.AlienSpawn;
import polly.rx.entities.DBAlienSpawn;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;

public class AlienSpawnModel extends AbstractHTMLTableModel<AlienSpawn> {

    private final static String[] COLUMNS = MSG.htmlAlienSpawnColumns.split(";"); //$NON-NLS-1$



    @Override
    public String getHeader(int column) {
        return COLUMNS[column];
    }



    @Override
    public boolean isFilterable(int column) {
        return column != COLUMNS.length - 1;
    }



    @Override
    public boolean isSortable(int column) {
        return column != COLUMNS.length - 1;
    }



    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }



    @Override
    public Object getCellValue(int column, AlienSpawn element) {
        final DBAlienSpawn dbas = (DBAlienSpawn) element;
        switch (column) {
        case 0:
            return element.getName();
        case 1:
            return element.getSector().toString();
        case 2:
            return element.getRace().toString();
        case 3:
            return new HTMLElement("input") //$NON-NLS-1$
                    .value(MSG.htmlAlienRaceRemove).attr("type", "button") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("class", "button") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("onclick", "removeSpawn(" + dbas.getId() + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
        return null;
    }



    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
        case 1:
        case 2:
            return String.class;
        default:
            return Object.class;
        }
    }



    @Override
    public List<AlienSpawn> getData(HttpEvent e) {
        final List<? extends AlienSpawn> spawns = Orion.INSTANCE.getAlienManager()
                .getAllSpawns();
        return new AbstractList<AlienSpawn>() {

            @Override
            public AlienSpawn get(int index) {
                return spawns.get(index);
            }



            @Override
            public int size() {
                return spawns.size();
            }
        };
    }
}
