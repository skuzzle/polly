package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.MSG;
import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElementGroup;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public class BattleReportModel extends AbstractHTMLTableModel<BattleReport> {

    private static final String REPORT_DETAILS_PNG = "/polly/rx/httpv2/view/report.png"; //$NON-NLS-1$
    private final static String DELETE_REPORT_PNG = "/polly/rx/httpv2/view/report_delete.png"; //$NON-NLS-1$

    private final static String[] COLUMNS = MSG.reportModelColumns.split(","); //$NON-NLS-1$
    
    protected final FleetDBManager fleetDb;
    
    
    public BattleReportModel(FleetDBManager fleetDb) {
        this.fleetDb = fleetDb;
        this.requirePermission(FleetDBManager.VIEW_BATTLE_REPORT_PERMISSION);
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
        return column < 12;
    }
    
    
    
    @Override
    public boolean isSortable(int column) {
        return column < 12;
    }
    
    
    
    @Override
    public Object getCellValue(int column, BattleReport element) {
        switch (column) {
        case 0: return element.getQuadrant();
        case 1: return element.getAttackerVenadName();
        case 2: return element.getAttackerClan();
        case 3: return element.getAttackerKw() / element.getAttackerBonus();
        case 4: return element.getAttackerXpMod();
        case 5: return element.getDefenderVenadName();
        case 6: return element.getDefenderClan();
        case 7: return element.getDefenderKw() / element.getDefenderBonus();
        case 8: return element.getDefenderXpMod();
        case 9: return element.getTactic();
        case 10: return element.hasArtifact();
        case 11: return element.getDate();
        case 12:
            final HTMLElementGroup result = new HTMLElementGroup();
            final HTMLElement detailLink = new HTMLElement("a") //$NON-NLS-1$
                .href(RXController.PAGE_REPORT_DETAILS + "?reportId=" + element.getId()) //$NON-NLS-1$
                .title(MSG.reportModelDetailTitle);
            final HTMLElement deleteLink = new HTMLElement("a") //$NON-NLS-1$
                .href("#").title(MSG.reportModelDeleteTitle) //$NON-NLS-1$
                .attr("onclick", "deleteReport(" + element.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            
            detailLink.content(
                new HTMLElement("img") //$NON-NLS-1$
                    .src(REPORT_DETAILS_PNG)
                    .attr("width", "20").attr("height", "20")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

            deleteLink.content(
                new HTMLElement("img").src(DELETE_REPORT_PNG) //$NON-NLS-1$
                    .attr("width", "20").attr("height", "20") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            );
            
            return result.add(detailLink).add(deleteLink);
        default: return ""; //$NON-NLS-1$
        }
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0: 
        case 1: 
        case 2: 
        case 5: 
        case 6: 
        case 9: return String.class;
        case 3: 
        case 4: 
        case 7: 
        case 8: return Double.class;
        case 10: return Boolean.class;
        case 11: return Date.class;
        default: return Object.class;
        }
    }
    
    
    
    @Override
    public int getDefaultSortColumn() {
        return 11;
    }
    
    
    
    @Override
    public SortOrder getDefaultSortOrder() {
        return SortOrder.DESCENDING;
    }
    
    

    @Override
    public List<BattleReport> getData(HttpEvent e) {
        return this.fleetDb.getAllReports();
    }
}
