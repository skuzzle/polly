package de.skuzzle.polly.tools.strings;

import java.io.PrintWriter;
import java.util.Iterator;

/**
 * This utility class provides methods to create strings from Iterators or Iterable
 * classes. Elements returned by an iterator can be conveniently printed into a string
 * with a custom separator.
 * 
 * @author Simon Taddiken
 */
public final class IteratorPrinter {
    
    
    /**
     * Interface to dynamically create different String representations of the same type.
     * 
     * @author Simon Taddiken
     * @param <T> Type of objects for which a String representation is created.
     */
    public static interface StringProvider<T> {
        
        /**
         * Creates a String representation for the provided instance of T.
         * 
         * @param o The object.
         * @return A String representation of that object.
         */
        public String toString(T o);
    }
    
    
    
    public final static <T> void print(Iterable<T> iterable, String separator,
            StringProvider<T> stringProvider, PrintWriter p) {
        print(iterable.iterator(), separator, stringProvider, p);
    }
    
    
    
    public final static <T> void print(Iterable<T> iterable, String separator, 
            PrintWriter p) {
        print(iterable.iterator(), separator, p);
    }
    
    

    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link PrintWriter}, separating them with the given separator String. The 
     * {@link #toString()} method is used to create Strings from each of the 
     * returned elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param p PrintWriter to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator, 
            PrintWriter p) {
        print(it, separator, new StringProvider<T>() {
            @Override
            public String toString(T o) {
                return o.toString();
            }
        }, p);
    }
    
    
    
    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link PrintWriter}, separating them with the given separator String. The
     * {@link #toString()} method is used to create Strings from each of the returned
     * elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param stringProvider 
     * @param p PrintWriter to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator,
            StringProvider<T> stringProvider, PrintWriter p) {
        while (it.hasNext()) {
            p.print(stringProvider.toString(it.next()));
            if (it.hasNext()) {
                p.print(separator);
            }
        }
    }
    
    
    
    private IteratorPrinter() {}
}
