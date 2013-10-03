package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElementGroup;
import de.skuzzle.polly.sdk.time.Milliseconds;

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
        case 5: return new Types.TimespanType(Milliseconds.toSeconds(element.getSpan()));
        case 6: return element.getEntries();
        case 7: return element.getDate();
        case 8: 
            final String v = element.getVenadName();
            return new HTMLElementGroup().add(
            new HTMLElement("a").href("#").content(
                    new HTMLElement("img").src("/polly/rx/httpv2/view/chart_curve_add.png")
                    .attr("width", "20").attr("height", "20")
                    .toString()).title("Compare")
                    .attr("onclick", "addToCompare('" + v.hashCode() + "', '" + v + "')")
            ).add(
            new HTMLElement("a").href("/pages/score/details?venadName=" + v).content(
                new HTMLElement("img").src("/polly/rx/httpv2/view/chart_curve.png")
                    .attr("width", "20").attr("height", "20")
                .toString()).title("View details")
            );
        default: return "";
        }
    }

    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 4: return Double.class;
        case 5: return Types.class;
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
