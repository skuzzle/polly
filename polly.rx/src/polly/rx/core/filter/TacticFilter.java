package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleTactic;
import de.skuzzle.polly.sdk.FormatManager;


public class TacticFilter implements BattleReportFilter {

    private BattleTactic tactic;
    
    
    public TacticFilter(BattleTactic tactic) {
        this.tactic = tactic;
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return report.getTactic() == this.tactic;
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Tactic is: " + this.tactic;
    }

    
    
    @Override
    public String getHint() {
        return "Matches only reports with the given tactic.";
    }
}
