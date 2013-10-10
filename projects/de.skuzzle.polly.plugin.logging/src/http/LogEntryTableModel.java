package http;

import java.util.Date;
import java.util.List;

import polly.logging.MSG;
import core.PollyLoggingManager;
import core.filters.SecurityLogFilter;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.roles.RoleManager;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;
import entities.LogEntry;

public class LogEntryTableModel extends AbstractHTMLTableModel<LogEntry> {

    private final static String[] COLUMNS = { 
        MSG.logTableDateCol, 
        MSG.logTableChannelCol, 
        MSG.logTableUserCol, 
        MSG.logTableTypeCol, 
        MSG.logTableMessageCol
    };

    public final static String RESULT_KEY = "logSearchResults"; //$NON-NLS-1$
    protected final PollyLoggingManager lm;
    protected final MyPolly myPolly;


    public LogEntryTableModel(PollyLoggingManager lm, MyPolly myPolly) {
        this.lm = lm;
        this.myPolly = myPolly;
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
    public boolean isFilterOnly() {
        return true;
    }



    @Override
    public Object getCellValue(int column, LogEntry element) {
        switch (column) {
        case 0: return element.getDate();
        case 1: return element.getChannel();
        case 2: return element.getNickname();
        case 3:
            switch (element.getType()) {
            default:
            case LogEntry.TYPE_JOIN: return MSG.logEntryTypeJoin;
            case LogEntry.TYPE_NICKCHANGE: return MSG.logEntryTypeNickchange;
            case LogEntry.TYPE_MESSAGE: return MSG.logEntryTypeMessage; 
            case LogEntry.TYPE_PART: return MSG.logEntryTypePart;
            case LogEntry.TYPE_QUIT: return MSG.logEntryTypeQuit;
            }
        case 4: return element.getMessage();
        default:
            return ""; //$NON-NLS-1$
        }
    }



    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return Date.class;
        default:
            return String.class;
        }
    }



    @Override
    public List<LogEntry> getData(HttpEvent e) {
        final User executor = (User) e.getSession().getAttached(WebinterfaceManager.USER);
        List<LogEntry> logs = this.lm.getAllEntries();
        if (!this.myPolly.roles().hasPermission(executor, RoleManager.ADMIN_PERMISSION)) {
            logs = this.lm.postFilter(logs, new SecurityLogFilter(this.myPolly, executor));
        }
        return logs;
    }
    
    
    @Override
    public int getDefaultSortColumn() {
        return 0;
    }
    
    
    
    @Override
    public SortOrder getDefaultSortOrder() {
        return SortOrder.DESCENDING;
    }
}
