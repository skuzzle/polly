package polly.rx.core.orion.model;

import de.skuzzle.polly.tools.Equatable;
import polly.rx.entities.BattleTactic;



public interface BattleReport extends OrionObject, Equatable {
    
    public BattleTactic getTactic();
    
    public Sector getSector();
    
    public Drop getDrop();
    
    public BattleReportCompetitor getWinner();
    
    public BattleReportCompetitor getLoser();
    
    public BattleReportCompetitor getAttacker();
    
    public BattleReportCompetitor getDefender();
}