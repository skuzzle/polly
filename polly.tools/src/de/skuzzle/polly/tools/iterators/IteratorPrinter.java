package de.skuzzle.polly.tools.iterators;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * This utility class provides methods to create strings from Iterators or Iterable
 * classes. Elements returned by an iterator can be conveniently printed into a string
 * with a custom separator. Different targets are supported 
 * ({@link StringBuilder}, {@link PrintWriter}).
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
    
    

    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link StringBuilder}, separating them with the given separator String. The 
     * {@link #toString()} method is used to create Strings from each of the 
     * returned elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param b StringBuilder to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator, 
            StringBuilder b) {
        print(it, separator, new StringProvider<T>() {
            @Override
            public String toString(T o) {
                return o.toString();
            }
        }, b);
    }
    
    
    
    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link StringBuilder}, separating them with the given separator String. The
     * provided {@link StringProvider} is used to create Strings from the objects returned
     * by the iterator.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param stringProvider Provider to create strings from the iterator's objects.
     * @param b StringBuilder to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator, 
            StringProvider<T> stringProvider, StringBuilder b) {
        
        while (it.hasNext()) {
            b.append(stringProvider.toString(it.next()));
            if (it.hasNext()) {
                b.append(separator);
            }
        }
    }
    
    
    
    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link PrintStream}, separating them with the given separator String. The 
     * {@link #toString()} method is used to create Strings from each of the 
     * returned elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param p PrintStream to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator, 
            PrintStream p) {
        print(it, separator, new StringProvider<T>() {
            @Override
            public String toString(T o) {
                return o.toString();
            }
        }, p);
    }
    
    
    
    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link PrintStream}, separating them with the given separator String. The
     * {@link #toString()} method is used to create Strings from each of the returned
     * elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param stringProvider 
     * @param p PrintStream to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator,
        StringProvider<T> stringProvider, PrintStream p) {
        while (it.hasNext()) {
            p.print(stringProvider.toString(it.next()));
            if (it.hasNext()) {
                p.print(separator);
            }
        }
    }
    
    
    
    private IteratorPrinter() {}
}
