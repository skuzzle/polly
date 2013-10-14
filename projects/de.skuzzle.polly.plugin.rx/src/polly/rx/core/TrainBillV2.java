package polly.rx.core;

import java.util.List;

import polly.rx.MSG;
import polly.rx.entities.TrainEntityV3;



public class TrainBillV2 {

    private List<TrainEntityV3> trains;
    
    
    
    public TrainBillV2(List<TrainEntityV3> trains) {
        this.trains = trains;
    }
    
    
    
    public List<TrainEntityV3> getTrains() {
        return this.trains;
    }
    
    
    
    private int weightedSumCache = -1;
    private int sumCache = -1;
    
    public synchronized int weightedSum() {
        if (this.weightedSumCache == -1) {
            this.weightedSumCache = 0;
            for (TrainEntityV3 train : this.trains) {
                this.weightedSumCache += train.getCosts() * train.getFactor();
            }
        }
        return this.weightedSumCache;
    }
    
    
    
    public synchronized int sum() {
        if (this.sumCache == -1) {
            this.sumCache = 0;
            for (TrainEntityV3 train : this.trains) {
                this.sumCache += train.getCosts();
            }
        }
        return this.sumCache;
    }
    
    
    
    @Override
    public String toString() {
        if (this.trains.isEmpty()) {
            return MSG.billNoOpen;
        } else {
            return MSG.bind(MSG.billOpen, this.trains.size(), this.weightedSum());
        }
    }
    
    
    
    public void closeBill() {
        for (TrainEntityV3 train : this.trains) {
            train.setClosed(true);
        }
    }
}
