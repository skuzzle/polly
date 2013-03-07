package polly.rx.core;

import polly.rx.entities.BattleReportShip;


public class SumQueries {

    public final static SumQuery PZ = new SumQuery() {
        @Override
        public int getValue(BattleReportShip ship) {
            return ship.getPz();
        }
    };
    
    
    
    public final static SumQuery PZ_DAMAGE = new SumQuery() {
        @Override
        public int getValue(BattleReportShip ship) {
            return ship.getPzDamage();
        }
    };
    
    
    
    public final static SumQuery TOTAL_DAMAGE = new SumQuery() {
        @Override
        public int getValue(BattleReportShip ship) {
            return ship.getPzDamage() + ship.getShieldDamage() + 
                ship.getStructureDamage();
        }
    };
    
    
    
    public final static SumQuery CAPI_XP = new SumQuery() {
        @Override
        public int getValue(BattleReportShip ship) {
            return ship.getCapiXp();
        }
    };
    
    
    
    public final static SumQuery CREW_XP = new SumQuery() {
        @Override
        public int getValue(BattleReportShip ship) {
            return ship.getCrewXp();
        }
    };
    
    
    private SumQueries() {}
}