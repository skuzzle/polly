package polly.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides Haskell style 'higher order' functions for list manipulation.
 * 
 * @author Simon
 *
 */
public class Functional {

    /**
     * Represents any operation which takes two parameters and returns a value.
     * 
     * @author Simon
     *
     * @param <A> The return type.
     * @param <B> The type of the first parameter.
     * @param <C> The type of the second parameter.
     */
    public interface BinaryOperation<A, B, C> {
        public A execute(B p1, C p2);
    }
    
    
    
    /**
     * Represents any operation which takes one parameter and returns a value.
     * 
     * @author Simon
     *
     * @param <A> The return type.
     * @param <B> The parameter type.
     */
    public interface UnaryOperation<A, B> {
        public A execute(B p);
    }
    
    
    
    /**
     * Represents a boolean function (predicate) which matches a given type
     * against any expression.
     * 
     * @author Simon
     *
     * @param <T> The type for this predicate.
     */
    public interface Predicate<T> extends UnaryOperation<Boolean, T> {
        public Boolean execute(T value);
    }
    
    
    
    /**
     *  it takes argument and the first item of the list and applies the 
     *  BinaryOperation to them, then feeds the function with this result and the 
     *  second argument and so on.
     *  

     * @param list The list to operate on
     * @param argument The argument to apply on first iteration.
     * @param op The BinaryOperation to apply on each item.
     * @return The fold result.
     */
    public static <A, B> A foldLeft(Collection<B> list, A argument,
            BinaryOperation<A, A, B> op) {
        
        A result = argument;
        for (B elem : list) {
            result = op.execute(result, elem);
        }
        return result;
    }
    
    
    
    /**
     * Applies the UnaryOperation to each item of the given list and returns a
     * new list which contains the results.
     * 
     * @param list The list to operate on.
     * @param op The UnaryOperation to apply on each item.
     * @return A new list of the same type which contains the results.
     */
    public static <A, B> Collection<B> map(List<A> list, UnaryOperation<B, A> op) {
        Collection<B> result = new ArrayList<B>();
        
        for (A elem : list) {
            result.add(op.execute(elem));
        }
        
        return result;
    }
    
    
    
    /**
     * Applies each UnaryOperation to each list item, but folds the result of the 
     * operations. The result of the application of the first UnaryOperation is
     * used as parameter for second application and so on.
     * 
     * @param list The list to operate on.
     * @param ops The operations to apply on each list item.
     * @return A new list which contains exactly as many items as the input list, but
     *      each item is a copy of the original.
     */
    public static <A, B> Collection<B> map(List<A> list, 
            Collection<? extends UnaryOperation<B, A>> ops) {
        
        Collection<B> result = new ArrayList<B>();
        
        for (A elem : list) {
            B r = null;
            for (UnaryOperation<B, A> op : ops) {
                r = op.execute(elem);
            }
            result.add(r);
        }
        
        return result;
    }
    
    
    
    /**
     * Check whether all listitems macth the given predicate.
     * 
     * @param p The predicate.
     * @param list The list to operate on.
     * @return true if all listitems match the predicate.
     */
    public static <T> boolean matchAll(final Predicate<T> p, Collection<T> list) {
        return Functional.foldLeft(list, true, 
                new BinaryOperation<Boolean, Boolean, T>() {
                    
                    @Override
                    public Boolean execute(Boolean p1, T p2) {
                        return p.execute(p2) && p1;
                    }
                });
    }
    
    
    
    /**
     * Creates a new list with every item of the given list which matches the given
     * predicate.
     * 
     * @param p The predicate to match the items.
     * @param list The list to operate on.
     * @return A new list of the same type which contains the results.
     */
    public static <T> Collection<T> filter(Predicate<T> p, Collection<T> list) {
        ArrayList<T> result = new ArrayList<T>();
        for (T elem : list) {
            if (p.execute(elem)) {
                result.add(elem);
            }
        }
        return result;
    }
    
    
    
    /**
     * Creates a new list with every item of the given list which does not match the 
     * given predicate.
     * 
     * @param p The predicate to match the items.
     * @param list The list to operate on.
     * @return A new list of the same type which contains the results.
     */
    public static <T> Collection<T> filterNot(final Predicate<T> p, Collection<T> list) {
        Predicate<T> p1 = new Predicate<T>() {
            @Override
            public Boolean execute(T argument) {
                return !p.execute(argument);
            }};

        return Functional.filter(p1, list);
    }
    
    
    
    /**
     * Checks whether any item of the list matches the given predicate and returns
     * the first occurence of this item.
     * 
     * @param p The predicate to match the items.
     * @param list The list to operate on.
     * @return The first result which have been found or null if no item of the list
     *      matches the predicate.
     */
    public static <T> T find(Predicate <T> p, Collection<T> list) {
        for (T elem : list) {
            if (p.execute(elem)) {
                return elem;
            }
        }
        return null;
    }
    
    
    
    /**
     * Creates a tupel of lists: the first contains all items from the input list 
     * that matches the predicate, the second contains all other item.
     * 
     * @param p The predicate to match the items.
     * @param list The list to operate on.
     * @return A tupel containing two lists (each may be empty but not null).
     */
    public static <T> Pair<Collection<T>, Collection<T>> seperate(Predicate<T> p, 
            Collection<T> list) {
        Collection<T> matches = new ArrayList<T>();
        Collection<T> other = new ArrayList<T>();
        
        for (T elem : list) {
            if (p.execute(elem)) {
                matches.add(elem);
            } else {
                other.add(elem);
            }
        }
        
        return new Pair<Collection<T>, Collection<T>>(matches, other);
    }
}
