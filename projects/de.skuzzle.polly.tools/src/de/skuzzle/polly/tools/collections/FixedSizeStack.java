package de.skuzzle.polly.tools.collections;

import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * This is a simple fixed size {@link Stack} implementation. If you try to push an
 * object onto the stack and it's capacity is exceeded, a {@link StackOverflowException}
 * will be thrown.
 * 
 * @author Simon Taddiken
 * @param <T> Type for elements in this stack.
 */
public class FixedSizeStack<T> implements Stack<T> {
    
    public static class StackOverflowException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private final T[] stack;
    private int sp;
    private int modcount;
    
    
    
    @SuppressWarnings("unchecked")
    public FixedSizeStack(int size) {
        this.stack = (T[]) new Object[size];
        this.sp = 0;
    }
    
    
    
    @Override
    public boolean isEmpty() {
        return this.sp == 0;
    }
    
    
    
    @Override
    public void push(T t) {
        if (this.sp == this.stack.length) {
            throw new StackOverflowException();
        }
        ++this.modcount;
        this.stack[this.sp++] = t;
    }
    
    
    
    @Override
    public T peek() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.stack[this.sp - 1];
    }
    
    
    
    @Override
    public T pop() {
        ++this.modcount;
        return this.stack[--this.sp];
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
            private int modc = modcount;
            
            @Override
            public boolean hasNext() {
                return this.i > 0;
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
