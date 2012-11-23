package de.skuzzle.polly.parsing;

import java.util.EmptyStackException;


/**
 * Simple stack interface. This was created because java itself does not have an interface
 * for stacks. As there a different types of stack implementations, a common interface 
 * for those seemed just logical. 
 * 
 * @author Simon Taddiken
 * @param <T> Type of elements in this stack.
 */
public interface Stack<T> {

    /**
     * Pushes an object onto the stack. The next call to {@link #peek()} or 
     * {@link #pop()} will return just that object.
     * 
     * @param t The object to push onto the stack.
     */
    public void push(T t);
    
    
    
    /**
     * Removes the element at the top of the stack and returns it. If you try to invoke 
     * this method on an empty stack, an {@link EmptyStackException} is thrown.
     * 
     * @return The element at the top of the stack.
     */
    public T pop();
    
    
    
    /**
     * Returns but does not remove the element at the top of the stack. If you try to invoke 
     * this method on an empty stack, an {@link EmptyStackException} is thrown.
     * 
     * @return The element at the top of the stack.
     */
    public T peek();
    
    
    
    /**
     * Gets the number of elements on this stack.
     * 
     * @return The stack's size.
     */
    public int size();
    
    
    
    /**
     * Gets whether this stack is empty.
     * 
     * @return <code>true</code> iff there is no element on this stack, <code>false</code>
     *          otherwise.
     */
    public boolean isEmpty();
}
