package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class DefenderClanFilter implements BattleReportFilter {

    private String defenderClan;
    
    
    public DefenderClanFilter(String defenderClan) {
        this.defenderClan = defenderClan;
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return report.getDefenderClan().equals(this.defenderClan);
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Defender Clan is: " + this.defenderClan;
    }
}
