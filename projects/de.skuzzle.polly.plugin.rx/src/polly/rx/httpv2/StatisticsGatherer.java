package polly.rx.httpv2;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import polly.rx.core.SumQueries;
import polly.rx.core.orion.Orion;
import polly.rx.core.orion.model.Production;
import polly.rx.entities.BattleDrop;
import polly.rx.entities.BattleReport;
import polly.rx.entities.BattleTactic;
import polly.rx.entities.RxRessource;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.HttpSession;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.WebinterfaceManager;
import de.skuzzle.polly.sdk.httpv2.html.HTMLModelListener;
import de.skuzzle.polly.sdk.httpv2.html.HTMLTableModel;
import de.skuzzle.polly.sdk.time.Time;
import de.skuzzle.polly.tools.math.MathUtil;


public class StatisticsGatherer implements HTMLModelListener<BattleReport> {
    
    public static class BattleReportStatistics {
        public final BattleDrop[] dropSum = new BattleDrop[14];
        public final BattleDrop[] dropMax = new BattleDrop[14];
        public final BattleDrop[] dropMin = new BattleDrop[14];
        public final BattleDrop[] repairCostAttacker = new BattleDrop[7];
        public final BattleDrop[] repairCostDefender = new BattleDrop[7];
        public final BattleDrop[] dropNetto = new BattleDrop[14];
        public double[] dropPrices = new double[14];
        public double[] dropPricesAtDropTime = new double[14];
        public double[] currentPrices = new double[14];
        
        public double kwAttacker = 0;
        public double kwDefender = 0;
        public int capiXpSumAttacker = 0;
        public int crewXpSumAttacker = 0;
        public int capiXpSumDefender = 0;
        public int crewXpSumDefender = 0;
        public int pzDamageAttacker = 0;
        public int pzDamageDefender = 0;
        public int artifacts = 0;
        public double repairTimeAttacker = 0;
        public double repairTimeDefender = 0;
        public int dropPriceSum;
        public int dropPriceSumAtDropTime;
        public double reportSize = 0; // as double for auto casting when calculating avg
        public double artifactChance = 0.0;
        
        
        BattleReportStatistics() {
            kwAttacker = 0;
            kwDefender = 0;
            capiXpSumAttacker = 0;
            crewXpSumAttacker = 0;
            capiXpSumDefender = 0;
            crewXpSumDefender = 0;
            pzDamageAttacker = 0;
            pzDamageDefender = 0;
            artifacts = 0;
            repairTimeAttacker = 0;
            repairTimeDefender = 0;
            reportSize = 0; 
            artifactChance = 0.0;
            dropPriceSum = 0;
            dropPriceSumAtDropTime = 0;
            BattleDrop.clear(dropSum);
            BattleDrop.clear(dropMax);
            BattleDrop.clear(dropMin);
            Arrays.fill(dropPrices, 0);
            Arrays.fill(dropPricesAtDropTime, 0);
        }
    }
    
    
    
    static void calculateNetto(BattleDrop[] dropSum, BattleDrop[] repairCost, 
            BattleDrop[] result) {
        assert dropSum.length == RxRessource.values().length && repairCost.length == 7 
                && result.length == dropSum.length;
        
        outer: for (int i = 0; i < dropSum.length; ++i) {
            assert dropSum[i] != null;
            // find matching repair cost:
            for (int j = 0; j < repairCost.length; ++j) {
                assert repairCost[j] != null;
                if (dropSum[i].getRessource() == repairCost[j].getRessource()) {
                    result[i] = new BattleDrop(dropSum[i].getRessource(), 
                            dropSum[i].getAmount() - repairCost[j].getAmount());
                    continue outer;
                }
            }
            // no value for this ress type
            result[i] = dropSum[i];
        }
    }
    
    
    
    static double inCr(BattleDrop[] drop, double[] prices, double result[]) {
        assert drop.length == prices.length;
        double sum = 0;
        for (int i = 0; i < drop.length; ++i) {
            result[i] = drop[i] != null
                    ? drop[i].getAmount() * prices[i]
                    : 0;
            sum += result[i];
        }
        return sum;
    }
    
    

    @Override
    public void onDataProcessed(HTMLTableModel<BattleReport> source,
            List<BattleReport> data, HttpEvent e) {
        
        final HttpSession s = e.getSession();
        final User user = (User) s.get(WebinterfaceManager.USER);
        final String STATISTIC_KEY = RXController.STATS_PREFIX + user.getName();
        
        final BattleReportStatistics stats = new BattleReportStatistics();
        s.set(STATISTIC_KEY, stats);

        Date youngest = null;
        
        synchronized (stats) {
        for (BattleReport report : data) {
            // do some filtering according to current sessions filter settings
            if (report.getTactic() == BattleTactic.ALIEN) {
                report = BattleReport.switchAttacker(report);
            }
            
            youngest = youngest == null 
                    ? report.getDate() 
                    : MathUtil.max(youngest, report.getDate());
            
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
            
            //report.calculateRepairTimes();
            stats.repairTimeAttacker += report.getAttackerRepairTimeOffset();
            stats.repairTimeDefender += report.getDefenderRepairTimeOffset();
            
            BattleDrop.sumUp(stats.repairCostAttacker, report.getAttackerRepairCostOffset());
            BattleDrop.sumUp(stats.repairCostDefender, report.getDefenderRepairCostOffset());
            
            stats.artifacts += report.hasArtifact() ? 1 : 0;
        }
        
        calculateNetto(stats.dropSum, stats.repairCostAttacker, stats.dropNetto);
        stats.reportSize = data.size();
        stats.kwAttacker /= data.size();
        stats.kwDefender /= data.size();
        
        stats.currentPrices = getPriceArray(null); // null for today
        stats.dropPriceSum = (int) inCr(stats.dropNetto, stats.currentPrices, stats.dropPrices);
        stats.dropPriceSumAtDropTime = (int) inCr(stats.dropNetto, getPriceArray(youngest), stats.dropPricesAtDropTime);
        stats.artifactChance = data.isEmpty() 
                ? 0.0 : (double) stats.artifacts / (double) data.size();
    }
    }
    
    
    
    private static double[] getPriceArray(Date date) {
        final double[] result = new double[RxRessource.values().length];
        final List<? extends Production> prod = Orion.INSTANCE.getPriceProvider().getAllPrices(date);
        int i = 0;
        for (final Production p : prod) {
            result[i++] = p.getRate();
        }
        return result;
    }
}
