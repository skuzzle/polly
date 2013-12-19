package de.skuzzle.polly.tools.io;

import java.util.ArrayDeque;
import java.util.Queue;


class SpeedHelper {
    
    public final static int SIZE = 20;

    private final Queue<Double> speedQueue;
    
    
    public SpeedHelper() {
        this.speedQueue = new ArrayDeque<>(SIZE);
    }
    
    
    
    public double calculateAvgSpeed() {
        double sum = 0.0;
        for (final double speed : this.speedQueue) {
            sum += speed;
        }
        if (sum == 0.0) {
            return 0.0;
        }
        return sum / this.speedQueue.size();
    }
    
    
    
    public void record(double speed) { 
        this.speedQueue.add(speed * 1000);
        if (this.speedQueue.size() > SIZE) {
            this.speedQueue.poll();
        }
    }
    
}
