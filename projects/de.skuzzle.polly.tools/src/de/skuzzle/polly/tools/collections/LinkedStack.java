package de.skuzzle.polly.tools.collections;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This {@link Stack} implementation uses a {@link LinkedList} as backend.
 * 
 * @author Simon Taddiken
 * @param <T> Type of the elements in this stack.
 */
public class LinkedStack<T> implements Stack<T> {

    private final LinkedList<T> backend;
    
    
    /**
     * Creates a new empty LinkedStack.
     */
    public LinkedStack() {
        this.backend = new LinkedList<T>();
    }
    
    
    
    /**
     * Creates a new LinkedStack by pushing all elements from the given collection onto 
     * it. The elements will be pushed in order they are returned by the given 
     * collection's iterator.
     * 
     * @param c Initial elements.
     */
    public LinkedStack(Collection<? extends T> c) {
        this();
        for (final T t : c) {
            this.push(t);
        }
    }
    
    
    
    @Override
    public void push(T t) {
        this.backend.addLast(t);
    }

    
    
    @Override
    public T pop() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.backend.removeLast();
    }
    
    

    @Override
    public T peek() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.backend.getLast();
    }
    
    

    @Override
    public int size() {
        return this.backend.size();
    }
    
    

    @Override
    public boolean isEmpty() {
        return this.backend.isEmpty();
    }
    
    
    
    @Override
    public Iterator<T> iterator() {
        return this.backend.descendingIterator();
    }
}
