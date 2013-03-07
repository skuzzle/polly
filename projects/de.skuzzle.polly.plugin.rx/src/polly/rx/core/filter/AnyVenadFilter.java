package polly.rx.core.filter;


import polly.rx.entities.BattleReport;


public class AnyVenadFilter extends BattleReportFilter {

    private String venad;
    
    
    public AnyVenadFilter(String venad) {
        this.venad = venad;
    }
    
    
    
    @Override
    public boolean acceptReport(BattleReport report) {
        return report.getAttackerVenadName().equals(this.venad) || 
            report.getDefenderVenadName().equals(this.venad);
    }
    
    

    @Override
    public String toString() {
        return "Involved venad is: " + this.venad;
    }

    
    
    @Override
    public String getHint() {
        return "Matches reports with the same attacker OR defender as given.";
    }
}
