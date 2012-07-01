package de.skuzzle.polly.tools.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class Interval implements Iterable<Integer> {
    
    public static Interval inclusive(int start, int end) {
        return new Interval(start, end + 1);
    }
    
    
    
    public static Interval exclusive(int start, int end) {
        return new Interval(start + 1, end);
    }
    
    
    public static Interval get(int end) {
        return new Interval(0, end);
    }
    
    
    
    public static Interval get(int start, int end) {
        return new Interval(start, end);
    }
    

    private int start;
    private int end;
    
    
    private Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    
    @Override
    public Iterator<Integer> iterator() {
        return new ItClass(this.start, this.end);
    }
    
    
    
    private static class ItClass implements Iterator<Integer> {

        private int max;
        private int current;
        
        public ItClass(int min, int max) {
            if (max < min) {
                throw new IllegalArgumentException("max < min: " + max + "<" + min);
            }
            this.max = max;
            this.current = min;
        }
        
        
        
        @Override
        public boolean hasNext() {
            return this.current < this.max;
        }

        @Override
        public Integer next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.current++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
        
    }

}