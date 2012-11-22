package de.skuzzle.polly.parsing;

import java.util.EmptyStackException;
import java.util.LinkedList;


public class LinkedStack<T> implements Stack<T> {

    private final LinkedList<T> backend;
    
    
    
    public LinkedStack() {
        this.backend = new LinkedList<T>();
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

}
