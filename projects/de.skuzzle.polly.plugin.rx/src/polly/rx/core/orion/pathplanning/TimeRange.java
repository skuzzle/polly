package polly.rx.core.orion.pathplanning;

public class TimeRange {

    private final int min;
    private final int max;



    public TimeRange(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException();
        }
        this.min = min;
        this.max = max;
    }


    
    public boolean notEmpty() {
        return this.max > 0;
    }
    
    

    public int getMin() {
        return this.min;
    }



    public int getMax() {
        return this.max;
    }
    
    
    
    @Override
    public String toString() {
        if (this.min == this.max) {
            return this.min + " Min"; //$NON-NLS-1$
        }
        return this.min + "-" + this.max + " Min"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}