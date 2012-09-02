package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class AttackerClanFilter implements BattleReportFilter {

    private String attackerClan;
    
    
    public AttackerClanFilter(String attackerClan) {
        this.attackerClan = attackerClan;
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return report.getAttackerClan().equals(this.attackerClan);
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Attacker Clan is: " + this.attackerClan;
    }
}
