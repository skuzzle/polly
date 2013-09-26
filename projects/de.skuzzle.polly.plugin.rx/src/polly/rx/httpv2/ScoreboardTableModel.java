package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;

public class ScoreboardTableModel extends AbstractHTMLTableModel<ScoreBoardEntry> {

    private final static String[] COLUMNS = { "Rank", "Venad", "Clan", "Points",
            "Points/day", "Timespan", "Entries", "Date", "Action" };


    protected final ScoreBoardManager sbManager;
    
    
    public ScoreboardTableModel(ScoreBoardManager sbManager) {
        this.sbManager = sbManager;
    }
    

    @Override
    public boolean isFilterable(int column) {
        return column < 8;
    }
    
    
    @Override
    public boolean isSortable(int column) {
        return column < 8;
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
    public Object getCellValue(int column, ScoreBoardEntry element) {
        switch (column) {
        case 0: return element.getRank();
        case 1: return element.getVenadName();
        case 2: return element.getClan();
        case 3: return element.getPoints();
        case 4: return element.getPointsPerDay();
        case 5:
        case 6: return element.getEntries();
        case 7: return element.getDate();
        case 8: return new HTMLElement("input")
            .attr("type", "button")
            .attr("class", "button")
            .attr("value", "Compare")
            .attr("onclick", "addToCompare('" + element.getVenadName() + "')");
        default: return "";
        }
    }

    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 4: return Double.class;
        case 7: return Date.class;
        default:
            return super.getColumnClass(column);
        }
    }
    


    @Override
    public List<ScoreBoardEntry> getData(HttpEvent e) {
        List<ScoreBoardEntry> entries = this.sbManager.getEntries();
        entries = ScoreBoardEntry.postFilter(entries);
        return entries;
    }
}
