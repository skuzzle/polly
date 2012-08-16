package polly.rx;

import java.util.List;

import polly.rx.entities.BattleReport;

import de.skuzzle.polly.sdk.PersistenceManager;


public class BattleReportManager {

    private PersistenceManager persistence;
    
    
    
    public BattleReportManager(PersistenceManager persistence) {
        this.persistence = persistence;
    }
    
    
    
    public List<BattleReport> getAllReports() {
        return this.persistence.atomicRetrieveList(BattleReport.class, 
            BattleReport.ALL_REPORTS);
    }
    
    
    
    public BattleReport getReportById(int id) {
        return this.persistence.atomicRetrieveSingle(BattleReport.class, id);
    }
}