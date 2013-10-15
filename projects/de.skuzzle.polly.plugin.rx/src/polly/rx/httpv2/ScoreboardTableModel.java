package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.MSG;
import polly.rx.MyPlugin;
import polly.rx.core.ScoreBoardManager;
import polly.rx.entities.ScoreBoardEntry;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElementGroup;
import de.skuzzle.polly.sdk.time.Milliseconds;

public class ScoreboardTableModel extends AbstractHTMLTableModel<ScoreBoardEntry> {

    private final static String[] COLUMNS = MSG.scoreboardModelColumns.split(","); //$NON-NLS-1$

    private final static String ADD_PNG = "/polly/rx/httpv2/view/chart_curve_add.png"; //$NON-NLS-1$
    private final static String CHART_CURVE_PNG = "/polly/rx/httpv2/view/chart_curve.png"; //$NON-NLS-1$
    

    protected final ScoreBoardManager sbManager;
    
    
    public ScoreboardTableModel(ScoreBoardManager sbManager) {
        this.sbManager = sbManager;
        this.requirePermission(MyPlugin.SBE_PERMISSION);
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
            new HTMLElement("a").href("#").content( //$NON-NLS-1$ //$NON-NLS-2$
                    new HTMLElement("img").src(ADD_PNG) //$NON-NLS-1$
                    .attr("width", "20").attr("height", "20")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    .title(MSG.scoreboardModelCompareTitle)
                    .attr("onclick", "addToCompare('" + v.hashCode() + "', '" + v + "')") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            ).add(
            new HTMLElement("a").href(RXController.PAGE_SCORE_DETAILS + "?venadName=" + v).content( //$NON-NLS-1$ //$NON-NLS-2$
                new HTMLElement("img").src(CHART_CURVE_PNG) //$NON-NLS-1$
                    .attr("width", "20").attr("height", "20")).title(MSG.scoreboardModelDetailsTitle) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            );
        default: return ""; //$NON-NLS-1$
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
