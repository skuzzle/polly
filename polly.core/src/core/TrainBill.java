package core;

import java.util.List;

import entities.TrainEntity;


public class TrainBill {

    private List<TrainEntity> trains;
    
    public TrainBill(List<TrainEntity> trains) {
        this.trains = trains;
    }
    
    
    private int sumCache = -1;
    
    public synchronized int sum() {
        if (this.sumCache == -1) {
            this.sumCache = 0;
            for (TrainEntity train : this.trains) {
                this.sumCache += train.getCost();
            }
        }
        return this.sumCache;
    }
    
    
    
    public List<TrainEntity> getTrains() {
        return this.trains;
    }
    
    
    
    public int trainCount() {
        return this.trains.size();
    }
    
    
    
    public void close() {
        for (TrainEntity train : this.trains) {
            train.setOpen(false);
        }
    }
    
    
    
    @Override
    public String toString() {
        if (this.trains.isEmpty()) {
            return "Keine offene Rechnung.";
        } else {
            return "Rechnung für " + this.trainCount() + " Trainings: " + 
                this.sum() + " Cr.";
        }
    }
}