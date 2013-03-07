package de.skuzzle.polly.tools.iterators;

import java.util.Iterator;



public class IndexIterator<T> implements Iterator<T> {

    private Iterator<T> wrapped;
    private int index;
    
    
    
    public IndexIterator(Iterable<T> iterable) {
        this(iterable.iterator(), -1);
    }
    
    
    
    public IndexIterator(Iterator<T> wrapped) {
        this(wrapped, -1);
    }
    
    
    
    public IndexIterator(Iterator<T> wrapped, int offset) {
        if (wrapped == null) {
            throw new NullPointerException("wrapped iterator can not be null");
        }
        this.wrapped = wrapped;
        this.index = offset;
    }
    
    
    
    public int getIndex() {
        return this.index;
    }
    
    
    
    @Override
    public boolean hasNext() {
        return this.wrapped.hasNext();
    }
    
    

    @Override
    public T next() {
        T tmp = this.wrapped.next();
        ++this.index;
        return tmp;
    }
    
    

    @Override
    public void remove() {
        this.wrapped.remove();
    }
}
