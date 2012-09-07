package polly.rx.core.filter;


import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleTactic;


public class TacticFilter extends BattleReportFilter {

    private BattleTactic tactic;
    
    
    public TacticFilter(BattleTactic tactic) {
        this.tactic = tactic;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getTactic() == this.tactic;
    }
    
    

    @Override
    public String toString() {
        return "Tactic is: " + this.tactic;
    }

    
    
    @Override
    public String getHint() {
        return "Matches only reports with the given tactic.";
    }
}
