package http;

import java.util.Date;
import java.util.List;

import core.RemindManager;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import entities.RemindEntity;

public class RemindTableModel extends AbstractHTMLTableModel<RemindEntity> {

    private final static String[] COLUMNS = { "Id", "Message", "Due", "Left", "Type",
            "For", "From", "Action" };

    private final RemindManager rm;



    public RemindTableModel(RemindManager rm) {
        this.rm = rm;
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
    public Object getCellValue(int column, RemindEntity element) {
        switch (column) {
        case 0:
        case 1: return String.class;
        case 2:
        case 3: return Date.class;
        case 4:
        case 5:
        case 6:
        case 7: return String.class;
        default: return Object.class;
        }
    }



    @Override
    public List<RemindEntity> getData() {
        return this.rm.getDatabaseWrapper().getAllReminds();
    }
}
