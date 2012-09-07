package polly.rx.core.filter;


import polly.rx.entities.BattleReport;


public class AttackerFilter extends BattleReportFilter {

    private String attacker;
    
    
    public AttackerFilter(String attacker) {
        this.attacker = attacker;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getAttackerVenadName().equals(this.attacker);
    }
    
    

    @Override
    public String toString() {
        return "Attacker is: " + this.attacker;
    }

    
    
    @Override
    public String getHint() {
        return "Matches reports with the same attacker as given.";
    }
}
