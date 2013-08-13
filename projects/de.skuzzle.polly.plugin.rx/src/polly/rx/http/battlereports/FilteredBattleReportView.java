package polly.rx.http.battlereports;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;
import de.skuzzle.polly.sdk.http.HttpTemplateException;
import de.skuzzle.polly.sdk.http.SimpleMultiPageView;


public abstract class FilteredBattleReportView extends SimpleMultiPageView<BattleReport> {

    protected final FleetDBManager fleetDBManager;
    
    public FilteredBattleReportView(String name, MyPolly myPolly, 
            FleetDBManager fleetDBManager) {
        super(name, myPolly, new BattleReportDataSource(fleetDBManager));
        this.fleetDBManager = fleetDBManager;
    }

    
    
    @Override
    protected String getSessionPageKey() {
        return "battlereports";
    }
    
    
    
    @Override
    protected abstract HttpTemplateContext createContext(HttpEvent e)
        throws HttpTemplateException, InsufficientRightsException;

}
