package polly.rx.core.filter;

import java.util.concurrent.atomic.AtomicInteger;

import polly.rx.entities.BattleReport;



public abstract class BattleReportFilter implements Comparable<BattleReportFilter> {
    
    final static class BattleReportFilterKey extends BattleReportFilter {
        
        public BattleReportFilterKey(int id) {
            super(id);
        }
        
        @Override
        protected boolean acceptReport(BattleReport report) {
            return false;
        }

        @Override
        public String getHint() {
            return null;
        }
    }

    public final static AtomicInteger ID_FACTORY = new AtomicInteger();
    
    private int id;
    private boolean negate;
    
    
    
    public BattleReportFilter() {
        this.id = ID_FACTORY.getAndIncrement();
    }
    
    
    
    BattleReportFilter(int id) {
        this.id = id;
    }
    
    
    
    public final boolean filter(BattleReport report) {
        return this.negate ^ this.acceptReport(report);
    }
    
    
    
    protected abstract boolean acceptReport(BattleReport report);
    
    public abstract String getHint();
    
    
    
    public final int getId() {
        return this.id;
    }
    
    
    
    public boolean isNegate() {
        return this.negate;
    }
    
    
    
    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof BattleReportFilter)) {
            return false;
        }
        BattleReportFilter other = (BattleReportFilter) obj;
        return this.getId() == other.getId() || this.toString().equals(obj.toString());
    }
    
    
    
    @Override
    public final int compareTo(BattleReportFilter other) {
        return this.toString().compareTo(other.toString());
    };
}
