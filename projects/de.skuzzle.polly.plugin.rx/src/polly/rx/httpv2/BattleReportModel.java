package polly.rx.httpv2;

import java.util.Date;
import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElement;
import de.skuzzle.polly.sdk.httpv2.html.HTMLElementGroup;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public class BattleReportModel extends AbstractHTMLTableModel<BattleReport> {

    private final static String[] COLUMNS = {
        "Quadrant", "Att.", "Att. Clan", "Att. KW (normalized)", "Att. XP-Mod",
        "Def.", "Def. Clan", "Def. KW (normalized)",  "Def. XP-Mod", 
        "Tactic", "Artifact", "Date", "Action"
    };
    
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
            final HTMLElement detailLink = new HTMLElement("a")
                .href("/pages/reportDetails?reportId=" + element.getId())
                .title("Report details");
            final HTMLElement deleteLink = new HTMLElement("a")
                .href("#").title("Delete report")
                .attr("onclick", "deleteReport(" + element.getId() + ")");
            
            detailLink.content(
                new HTMLElement("img")
                    .src("/polly/rx/httpv2/view/report.png")
                    .attr("width", "20").attr("height", "20"));

            deleteLink.content(
                new HTMLElement("img").src("/polly/rx/httpv2/view/report_delete.png")
                    .attr("width", "20").attr("height", "20")
            );
            
            return result.add(detailLink).add(deleteLink);
        default: return "";
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
        case 4: return Double.class;
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
