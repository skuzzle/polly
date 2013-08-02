package de.skuzzle.polly.sdk.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;


/**
 * <p>Type unsafe comparator implementation that uses reflection to retrieve an objects
 * getter method and then compares its result to the result of the others object getter.
 * The getter can be specified as String.</p>
 * 
 * <p>There are several things that can go wrong by using this comparator. Any violation
 * of the following rules will result in an {@link CompareException} being thrown.</p>
 * 
 * <p>First, both objects that are being compared must have a getter method with no 
 * parameter and same return type with the name specified in the constructor of this 
 * class. Second, the return type of that getter method must implement the 
 * {@link Comparable} interface. Third, there are other things that can go wrong when
 * using exceptions such like illegal access when a SecurityManager is used or
 * the invoked methods themself throw any runtime exceptions. This implementations does
 * not care for them and just delegates them to the caller, causing the compare-process
 * for those objects to fail.</p>
 * 
 * @author Simon
 * @since 0.9.1
 */
public class ReflectionComparator implements Comparator<Object>{
    

    private String field;
    private boolean desc;
    
    
    
    /**
     * Creates a new Comparator that can sort objects that have a getter with the given
     * fieldName. The created Comparator will always sort ascending.
     * 
     * @param fieldName The name of the getter that will be used as sort key.
     */
    public ReflectionComparator(String fieldName) {
        this(fieldName, false);
    }
    
    
    
    /**
     * Creates a new Comparator that can sort objects that have a getter with the given
     * fieldName. If desc is set <code>true</code>, this comparator will sort 
     * descending, ascending otherwise.
     * 
     * @param fieldName The name of the getter that will be used as sort key.
     * @param desc The sort direction.
     */
    public ReflectionComparator(String fieldName, boolean desc) {
        this.field = fieldName;
        this.desc = desc;
    }
    
    
    
    public String getField() {
        return this.field;
    }
    
    
    
    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object o1, Object o2) {
        try {
            Method getter1 = o1.getClass().getMethod(this.field);
            Method getter2 = o2.getClass().getMethod(this.field);
            
            Comparable<Object> result1 = (Comparable<Object>) getter1.invoke(o1);
            Comparable<Object> result2 = (Comparable<Object>) getter2.invoke(o2);
            
            if (this.desc) {
                return result2.compareTo(result1);
            } else {
                return result1.compareTo(result2);
            }
        } catch (NoSuchMethodException e) {
            throw new CompareException("Specified getter '" + 
        this.field + "' does not exist", e);
        } catch (SecurityException e) {
            throw new CompareException(e);
        } catch (IllegalAccessException e) {
            throw new CompareException(e);
        } catch (IllegalArgumentException e) {
            throw new CompareException(e);
        } catch (InvocationTargetException e) {
            throw new CompareException(e);
        }
    }

}
