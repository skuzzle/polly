package http;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.RemindManager;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.Types.DateType;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import entities.RemindEntity;

public class MyRemindTableModel extends AbstractHTMLTableModel<RemindEntity> {

    private final static String[] COLUMNS = { "Id", "Message", "Due", "Left", "Type",
            "For", "From", "Action" };

    protected final RemindManager rm;



    public MyRemindTableModel(RemindManager rm) {
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
    public boolean isFilterable(int column) {
        return column < 7;
    }
    
    
    
    @Override
    public boolean isSortable(int column) {
        return column < 7;
    }
    
    
    
    @Override
    public boolean isEditable(int column) {
        return column == 1 || column == 2;
    }

    
    
    @Override
    public SuccessResult setCellValue(int column, RemindEntity element, String value,
            User executor, MyPolly myPolly) {
        if (column == 1) {
            try {
                this.rm.modifyRemind(executor, element.getId(), 
                    element.getDueDate(), value);
                return new SuccessResult(true, value);
            } catch (CommandException | DatabaseException e) {
                return new SuccessResult(false, e.getMessage());
            }
        } else if (column == 2) {
            final Types d = myPolly.parse(value);
            if (!(d instanceof Types.DateType)) {
                return new SuccessResult(false, "Not a valid date");
            }
            final Date date = ((DateType) d).getValue();
            try {
                this.rm.modifyRemind(executor, element.getId(), 
                    date, element.getMessage());
                return new SuccessResult(true, myPolly.formatting().formatDate(date));
            } catch (CommandException | DatabaseException e) {
                return new SuccessResult(false, e.getMessage());
            }
        }
        return super.setCellValue(column, element, value, executor, myPolly);
    }


    @Override
    public Object getCellValue(int column, RemindEntity element) {
        switch (column) {
        case 0: return element.getId();
        case 1: return element.getMessage();
        case 2: return element.getDueDate();
        case 3: return element.getLeaveDate();
        case 4: return "?";
        case 5: return element.getForUser();
        case 6: return element.getFromUser();
        case 7: return new HTMLElement("input")
            .attr("value", "Cancel")
            .attr("type", "button")
            .attr("class", "button")
            .attr("onclick", "cancelRemind("+element.getId()+")");
        default: return "";
        }
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int column) {
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
    public Map<String, String> getRequestParameters(HttpEvent e) {
        final Map<String, String> m = new HashMap<>();
        final User user = (User) e.getSession().getAttached("user");
        m.put("nickname", user.getName());
        return m;
    }


    
    @Override
    public List<RemindEntity> getData(HttpEvent e) {
        return this.rm.getDatabaseWrapper().getMyRemindsForUser(e.get("nickname"));
    }
}
