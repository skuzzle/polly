package polly.util;

import java.util.Iterator;

import polly.util.Functional.UnaryOperation;


public class Sequence<T> implements Iterable<T>, Iterator<T> {
    
    private UnaryOperation<T, T> op;
    private T currentVal;
    
    
    
    public Sequence(T startval, UnaryOperation<T, T> op) {
        this.op = op;
        this.currentVal = startval;
    }
    
    
    
    public T getCurrent() {
        return this.currentVal;
    }
    
    
    
    @Override
    public boolean hasNext() {
        return true;
    }
    
    

    @Override
    public T next() {
        this.currentVal = this.op.execute(this.currentVal);
        return this.currentVal;
    }
    
    

    @Override
    public void remove() {
        throw new RuntimeException("not implemented");
    }
    


    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
