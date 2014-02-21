package polly.rx.core.orion.model;

import java.util.List;

import de.skuzzle.polly.tools.Equatable;


public interface BattleReportCompetitor extends VenadOwner, Equatable {

    public boolean isWinner();
    
    public String getFleetName();
    
    public float getKw();
    
    public float getXpMod();
    
    public List<? extends ReportShip> getShips();
}
