package de.skuzzle.polly.tools.streams;


class Allocation {
    
    final static int ALLOCATION_HISTORY_SIZE = 10;

    long allocationTime;
    int bytes;
    
    public Allocation(long allocationTime, int bytes) {
        super();
        this.allocationTime = allocationTime;
        this.bytes = bytes;
    }
}
