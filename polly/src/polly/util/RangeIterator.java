package polly.util;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class RangeIterator implements Iterable<Integer> {
    
    public static RangeIterator get(int end) {
        return new RangeIterator(0, end);
    }
    
    
    
    public static RangeIterator get(int start, int end) {
        return new RangeIterator(start, end);
    }
    

    private int start;
    private int end;
    
    
    private RangeIterator(int start, int end) {
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