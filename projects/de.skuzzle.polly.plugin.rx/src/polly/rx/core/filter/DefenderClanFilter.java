package polly.rx.core.filter;


import polly.rx.entities.BattleReport;


public class DefenderClanFilter extends BattleReportFilter {

    private String defenderClan;
    
    
    public DefenderClanFilter(String defenderClan) {
        this.defenderClan = defenderClan;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getDefenderClan().equals(this.defenderClan);
    }
    
    

    @Override
    public String toString() {
        return "Defender Clan is: " + this.defenderClan;
    }
    
    
    
    public String getHint() {
        return "Matches reports with the same defender clan as given.";
    };
}
