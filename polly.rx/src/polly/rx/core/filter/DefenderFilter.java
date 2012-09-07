package polly.rx.core.filter;

import polly.rx.entities.BattleReport;


public class DefenderFilter extends BattleReportFilter {

    private String defender;
    
    
    public DefenderFilter(String defender) {
        this.defender = defender;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getDefenderVenadName().equals(this.defender);
    }
    
    

    @Override
    public String toString() {
        return "Defender is: " + this.defender;
    }
    
    
    
    @Override
    public String getHint() {
        return "Matches reports with the same defender as given.";
    }
}
