package polly.rx.httpv2;

import java.util.List;

import polly.rx.core.SumQueries;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleTactic;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.html.HTMLModelListener;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTableModel;


public class StatisticsGatherer implements HTMLModelListener<BattleReport> {
    
    public static class BattleReportStatistics {
        public final BattleDrop[] dropSum = new BattleDrop[14];
        public final BattleDrop[] dropMax = new BattleDrop[14];
        public final BattleDrop[] dropMin = new BattleDrop[14];
        public final BattleDrop[] repairCostAttacker = new BattleDrop[7];
        public final BattleDrop[] repairCostDefender = new BattleDrop[7];
        
        public double kwAttacker = 0;
        public double kwDefender = 0;
        public int capiXpSumAttacker = 0;
        public int crewXpSumAttacker = 0;
        public int capiXpSumDefender = 0;
        public int crewXpSumDefender = 0;
        public int pzDamageAttacker = 0;
        public int pzDamageDefender = 0;
        public int artifacts = 0;
        public int repairTimeAttacker = 0;
        public int repairTimeDefender = 0;
        public int reportSize = 0;
        public double artifactChance = 0.0;
    }
    
    

    @Override
    public void onDataProcessed(HTMLTableModel<BattleReport> source,
            List<BattleReport> data, HttpEvent e) {
        
        final HttpSession s = e.getSession();
        final User user = (User) s.getAttached("user");
        final String STATISTIC_KEY = "BR_STATS_" + user.getName();
        
        final BattleReportStatistics stats = new BattleReportStatistics();
        s.set(STATISTIC_KEY, stats);

        synchronized (stats) {
        for (BattleReport report : data) {
            // do some filtering according to current sessions filter settings
            if (report.getTactic() == BattleTactic.ALIEN) {
                report = BattleReport.switchAttacker(report);
            }
            
            for (int i = 0; i < 14; ++i) {
                BattleDrop drop = report.getDrop().get(i);
                
                if (stats.dropSum[i] == null) {
                    stats.dropSum[i] = new BattleDrop(drop.getRessource(),
                        drop.getAmount());
                    stats.dropMin[i] = new BattleDrop(drop.getRessource(),
                        drop.getAmount());
                    stats.dropMax[i] = new BattleDrop(drop.getRessource(),
                        drop.getAmount());
                } else {
                    stats.dropSum[i].incAmout(drop);
                    stats.dropMin[i].setAmount(Math.min(stats.dropMin[i].getAmount(), 
                        drop.getAmount()));
                    stats.dropMax[i].setAmount(Math.max(stats.dropMax[i].getAmount(), 
                        drop.getAmount()));
                }
            }
            
            stats.kwAttacker += report.getAttackerKw() / report.getAttackerBonus();
            stats.kwDefender += report.getDefenderKw() / report.getDefenderBonus();
            stats.capiXpSumAttacker += report.querySumAttacker(SumQueries.CAPI_XP);
            stats.crewXpSumAttacker += report.querySumAttacker(SumQueries.CREW_XP);
            stats.capiXpSumDefender+= report.querySumDefender(SumQueries.CAPI_XP);
            stats.crewXpSumDefender += report.querySumDefender(SumQueries.CREW_XP);
            stats.pzDamageAttacker += report.querySumAttacker(SumQueries.PZ_DAMAGE);
            stats.pzDamageDefender += report.querySumDefender(SumQueries.PZ_DAMAGE);
            
            report.calculateRepairTimes();
            stats.repairTimeAttacker += report.getAttackerRepairTimeOffset();
            stats.repairTimeDefender += report.getDefenderRepairTimeOffset();
            
            BattleDrop.sumUp(stats.repairCostAttacker, report.getAttackerRepairCostOffset());
            BattleDrop.sumUp(stats.repairCostDefender, report.getDefenderRepairCostOffset());
            
            stats.artifacts += report.hasArtifact() ? 1 : 0;
        }
        
        stats.reportSize = data.size();
        stats.kwAttacker /= data.size();
        stats.kwDefender /= data.size();
        stats.artifactChance = data.isEmpty() 
                ? 0.0 : (double) stats.artifacts / (double) data.size();
    }
    }
}
