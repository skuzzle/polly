package polly.rx.core.filter;

import polly.rx.entities.BattleReport;
import de.skuzzle.polly.sdk.FormatManager;


public class AttackerFilter implements BattleReportFilter {

    private String attacker;
    
    
    public AttackerFilter(String attacker) {
        this.attacker = attacker;
    }
    
    
    
    @Override
    public boolean accept(BattleReport report) {
        return report.getAttackerVenadName().equals(this.attacker);
    }
    
    

    @Override
    public String toString(FormatManager formatManager) {
        return "Attacker is: " + this.attacker;
    }

}
