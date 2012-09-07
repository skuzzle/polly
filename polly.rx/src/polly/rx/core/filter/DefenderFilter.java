package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class DefenderFilter implements BattleReportFilter {

    private String defender;
    
    
    public DefenderFilter(String defender) {
        this.defender = defender;
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return report.getDefenderVenadName().equals(this.defender);
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Defender is: " + this.defender;
    }
    
    
    
    @Override
    public String getHint() {
        return "Matches reports with the same defender as given.";
    }
}
