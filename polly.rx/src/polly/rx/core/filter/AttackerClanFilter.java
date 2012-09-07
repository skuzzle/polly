package polly.rx.core.filter;


import polly.rx.entities.BattleReport;


public class AttackerClanFilter extends BattleReportFilter {

    private String attackerClan;
    
    
    public AttackerClanFilter(String attackerClan) {
        this.attackerClan = attackerClan;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getAttackerClan().equals(this.attackerClan);
    }
    
    

    @Override
    public String toString() {
        return "Attacker Clan is: " + this.attackerClan;
    }
    
    
    
    @Override
    public String getHint() {
        return "Matches reports with the same attacker clan as given.";
    }
}
