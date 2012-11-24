package de.skuzzle.polly.tools.iterators;

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
     * Prints all elements returned by the iterable's iterator into the given
     * {@link StringBuilder}, separating them with the given separator String. The 
     * {@link #toString()} method is used to create Strings from each of the 
     * returned elements.
     * 
     * @param iterable Iterable that provides the iterator.
     * @param separator Separator string which will be put between the elements.
     * @param b StringBuilder to print the result to.
     */
    public final static <T> void print(Iterable<T> iterable, String separator, 
            StringBuilder b) {
        print(iterable.iterator(), separator, b);
    }
    
    
    
    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link StringBuilder}, separating them with the given separator String. The
     * {@link #toString()} method is used to create Strings from each of the returned
     * elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param b StringBuilder to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator, 
            StringBuilder b) {
        while (it.hasNext()) {
            b.append(it.next().toString());
            if (it.hasNext()) {
                b.append(separator);
            }
        }
    }
    
    
    
    /**
     * Prints all elements returned by the iterable's iterator into the given
     * {@link PrintWriter}, separating them with the given separator String. The 
     * {@link #toString()} method is used to create Strings from each of the 
     * returned elements.
     * 
     * @param iterable Iterable that provides the iterator.
     * @param separator Separator string which will be put between the elements.
     * @param w PrintWriter to print the result to.
     */
    public final static <T> void print(Iterable<T> iterable, String separator, 
            PrintWriter w) {
        print(iterable.iterator(), separator, w);
    }
    
    
    
    /**
     * Prints all elements returned by the given iterator into the given 
     * {@link PrintWriter}, separating them with the given separator String. The
     * {@link #toString()} method is used to create Strings from each of the returned
     * elements.
     * 
     * @param it Iterator that provides the elements to print.
     * @param separator Separator string which will be put between the elements.
     * @param w PrintWriter to print the result to.
     */
    public final static <T> void print(Iterator<T> it, String separator, PrintWriter w) {
        while (it.hasNext()) {
            w.write(it.next().toString());
            if (it.hasNext()) {
                w.write(separator);
            }
        }
    }
    
    
    
    private IteratorPrinter() {}
}
