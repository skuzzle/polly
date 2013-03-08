package de.skuzzle.polly.tools.collections;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Dynamic sized stack implementation based on a growing array.
 *  
 * @author Simon Taddiken
 * @param <T> Type of elements in this stack.
 */
public class ArrayStack<T> implements Stack<T> {

    /** Initial size for no-args constructor. */
    public final static int DEFAULT_SIZE = 16;
    
    
    
    private T[] stack;
    private int sp;
    private int modcount;
    
    
    
    /**
     * Creates a new ArrayStack with a {@link #DEFAULT_SIZE} of {@value #DEFAULT_SIZE}.
     */
    public ArrayStack() {
        this(DEFAULT_SIZE);
    }
    
    
    
    /**
     * Creates a new ArrayStack with the given initial capacity.
     * @param capacity The initial capacity of the stack
     */
    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        this.stack = (T[]) new Object[capacity];
    }
    
    

    /**
     * Creates a new ArrayStack by pushing all elements from the given collection onto 
     * it. The elements will be pushed in order they are returned by the given 
     * collection's iterator.
     * 
     * @param c Initial elements.
     */
    public ArrayStack(Collection<? extends T> c) {
        this(c.size());
        for (final T t : c) {
            this.push(t);
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    private void ensureCapacity(int needed) {
        if (this.stack.length == needed) {
            final T[] tmp = (T[]) new Object[this.stack.length * 2];
            System.arraycopy(this.stack, 0, tmp, 0, this.stack.length);
            this.stack = tmp;
        }
    }
    
    
    
    @Override
    public void push(T t) {
        ++this.modcount;
        this.ensureCapacity(this.sp + 1);
        this.stack[this.sp++] = t;
    }
    
    

    @Override
    public T pop() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        ++this.modcount;
        return this.stack[--this.sp];
    }

    
    
    @Override
    public T peek() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.stack[this.sp - 1];
    }
    
    

    @Override
    public int size() {
        return this.sp;
    }
    
    

    @Override
    public boolean isEmpty() {
        return this.sp == 0;
    }
    
    

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = sp;
            int modc = modcount;
            
            @Override
            public boolean hasNext() {
                return i > 0;
            }
            
            

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (modc != modcount) {
                    throw new ConcurrentModificationException();
                }
                return stack[--i];
            }
            
            

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
