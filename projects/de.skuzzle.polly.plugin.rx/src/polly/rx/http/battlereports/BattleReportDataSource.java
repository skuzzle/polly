package polly.rx.http.battlereports;

import java.util.List;

import polly.rx.core.FleetDBManager;
import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.http.MultiPageDataSource;


public class BattleReportDataSource implements MultiPageDataSource<BattleReport>{

    final FleetDBManager fleetDBManager;
    
    
    public BattleReportDataSource(FleetDBManager fleetDBManager) {
        this.fleetDBManager = fleetDBManager;
    }

    

    @Override
    public int getDataCount() {
        return this.fleetDBManager.getBattleReportCount();
    }



    @Override
    public List<BattleReport> getSubData(int first, int limit) {
        return this.fleetDBManager.battleReportRange(first, limit);
    }

}
