package polly.rx.httpv2;

import java.util.AbstractList;
import java.util.List;

import polly.rx.MSG;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.model.AlienRace;
import polly.rx.core.orion.model.DefaultAlienRace;
import polly.rx.entities.DBAlienRace;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;

public class AlienRaceModel extends AbstractHTMLTableModel<AlienRace> {

    private final static String[] COLUMNS = MSG.htmlAlienRaceColumns.split(";"); //$NON-NLS-1$



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
        // all except the last
        return column != COLUMNS.length - 1;
    }



    @Override
    public boolean isSortable(int column) {
        // all except the last
        return column != COLUMNS.length - 1;
    }

    
    
    @Override
    public boolean isEditable(int column) {
        return column == 1;
    }
    
    
    
    @Override
    public SuccessResult setCellValue(int column, AlienRace element, String value,
            User executor, MyPolly myPolly) {
        
        final DBAlienRace dbar = (DBAlienRace) element;
        AlienRace update = null;
        switch (column) {
        case 1:
            update = new DefaultAlienRace(element.getName(), value, 
                    element.isAggressive());
            break;
        default:
            break;
        }
        
        if (update != null) {
            try (final Write w = myPolly.persistence().write()) {
                dbar.updateWith(update);
                return new SuccessResult(true, ""); //$NON-NLS-1$
            } catch (DatabaseException e) {
                return new SuccessResult(false, e.getMessage());
            }
        }
        
        return super.setCellValue(column, element, value, executor, myPolly);
    }
    


    @Override
    public Object getCellValue(int column, AlienRace element) {
        final DBAlienRace dba = (DBAlienRace) element;
        switch (column) {
        case 0:
            return element.getName();
        case 1:
            return element.getSubName();
        case 2:
            return element.isAggressive();
        case 3:
            return new HTMLElement("input") //$NON-NLS-1$
                    .value(MSG.htmlAlienRaceRemove).attr("type", "button") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("class", "button") //$NON-NLS-1$ //$NON-NLS-2$
                    .attr("onclick", "removeRace(" + dba.getId() + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
        return null;
    }



    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
        case 1:
            return String.class;
        case 2:
            return Boolean.class;
        default:
            return Object.class;
        }
    }



    @Override
    public List<AlienRace> getData(HttpEvent e) {
        final List<? extends AlienRace> races = Orion.INSTANCE.getAlienManager()
                .getAllRaces();
        return new AbstractList<AlienRace>() {

            @Override
            public AlienRace get(int i) {
                return races.get(i);
            }



            @Override
            public int size() {
                return races.size();
            }
        };
    }
}
