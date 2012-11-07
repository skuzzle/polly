package de.skuzzle.polly.parsing.execution;

import java.util.EmptyStackException;

/**
 * This is a simple fixed size stack.
 * 
 * @author Simon Taddiken
 *
 * @param <T> Type for elements in this stack.
 */
public class FixedSizeStack<T> {

    private final T[] stack;
    private int sp;
    
    
    
    @SuppressWarnings("unchecked")
    public FixedSizeStack(int size) {
        this.stack = (T[]) new Object[size];
        this.sp = -1;
    }
    
    
    
    public boolean isEmpty() {
        return this.sp == -1;
    }
    
    
    
    public int push(T t) {
        if (this.sp + 1 == this.stack.length) {
            throw new StackOverflowException();
        }
        this.stack[this.sp++] = t;
        return this.sp;
    }
    
    
    
    public T peek() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.stack[this.sp];
    }
    
    
    
    public T pop() {
        final T t = this.peek();
        --this.sp;
        return t;
    }
    
    
    
    public int addressOf(T t) {
        for(int i = 0; i < this.sp; ++i) {
            if(this.stack[i] != null && this.stack[i].equals(t)) {
                return i;
            }
        }
        return -1;
    }
}
