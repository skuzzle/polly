package polly.rx.http;

import java.util.List;

import polly.rx.core.SumQueries;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.RxRessource;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class TemplateContextHelper {

    public final static void prepareForReportsList(HttpTemplateContext c, 
        List<BattleReport> reports) {
        
        BattleDrop[] dropSum = new BattleDrop[14];
        int capiXpSumAttacker = 0;
        int crewXpSumAttacker = 0;
        int capiXpSumDefender = 0;
        int crewXpSumDefender = 0;
        int pzDamageAttacker = 0;
        int pzDamageDefender = 0;
        
        for (BattleReport report : reports) {
            for (int i = 0; i < 14; ++i) {
                if (dropSum[i] == null) {
                    dropSum[i] = new BattleDrop(RxRessource.byOrdinal(i), 
                        report.getDrop().get(i).getAmount());
                } else {
                    dropSum[i].incAmout(report.getDrop().get(i).getAmount());
                }
            }
            capiXpSumAttacker += report.querySumAttacker(SumQueries.CAPI_XP);
            crewXpSumAttacker += report.querySumAttacker(SumQueries.CREW_XP);
            capiXpSumDefender+= report.querySumDefender(SumQueries.CAPI_XP);
            crewXpSumDefender += report.querySumDefender(SumQueries.CREW_XP);
            pzDamageAttacker += report.querySumAttacker(SumQueries.PZ_DAMAGE);
            pzDamageDefender += report.querySumDefender(SumQueries.PZ_DAMAGE);
        }
        
        c.put("capiXpSumAttacker", capiXpSumAttacker);
        c.put("crewXpSumAttacker", crewXpSumAttacker);
        c.put("capiXpSumDefender", capiXpSumDefender);
        c.put("crewXpSumDefender", crewXpSumDefender);
        c.put("pzDamageAttacker", pzDamageAttacker);
        c.put("pzDamageDefender", pzDamageDefender);
        
        c.put("dropSum", dropSum);
        c.put("allReports", reports);
    }
    
    
    
    private TemplateContextHelper() {}
}