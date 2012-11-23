package de.skuzzle.polly.parsing;

import java.util.EmptyStackException;
import java.util.Iterator;


/**
 * This is a simple fixed size {@link Stack} implementation. If you try to push an
 * object onto the stack and it's capacity is exceeded, a {@link StackOverflowException}
 * will be thrown.
 * 
 * @author Simon Taddiken
 * @param <T> Type for elements in this stack.
 */
public class FixedSizeStack<T> implements Stack<T>{

    private final T[] stack;
    private int sp;
    
    
    
    @SuppressWarnings("unchecked")
    public FixedSizeStack(int size) {
        this.stack = (T[]) new Object[size];
        this.sp = -1;
    }
    
    
    
    @Override
    public boolean isEmpty() {
        return this.sp == -1;
    }
    
    
    
    @Override
    public void push(T t) {
        if (this.sp + 1 == this.stack.length) {
            throw new StackOverflowException();
        }
        this.stack[this.sp++] = t;
    }
    
    
    
    @Override
    public T peek() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.stack[this.sp];
    }
    
    
    
    public T peek(int n) {
        if (this.sp - n < 0) {
            throw new IllegalArgumentException("illegal peek value: " + n);
        }
        return this.stack[this.sp - n];
    }
    
    
    
    @Override
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



    @Override
    public int size() {
        return this.sp;
    }



    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = sp;
            
            
            @Override
            public boolean hasNext() {
                return this.i != 0;
            }

            @Override
            public T next() {
                return stack[--i];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
