package polly.util;

/**
 * Represents an immutable pair of objects.
 * 
 * @author Simon
 *
 * @param <T1> Type of first argument of this pair.
 * @param <T2> Type of second argument of this pair.
 */
public class Pair<T1, T2> {
    
    private T1 first;
    
    private T2 second;
    
    
    
    /**
     * Constructs a pair with two given objects.
     * 
     * @param first First object of this pair.
     * @param second Second object of this pair.
     */
    public Pair(T1 first, T2 second) {
        this.first  = first;
        this.second = second;
    }
    
    
    
    /**
     * Constructs a pair by copying another pair. The objects the other
     * pair contains are not copied, which means that both pairs, the newly created
     * and the passed, reference the same objects.
     * 
     * @param other The pair to copy.
     */
    public Pair(Pair<T1, T2> other) {
        this.first = other.getFirst();
        this.second = other.getSecond();
    }


    
    public T1 getFirst() {
        return first;
    }
    
    
    public void setFirst(T1 first) {
        this.first = first;
    }

    

    public T2 getSecond() {
        return second;
    }
    
    
    public void setSecond(T2 second) {
        this.second = second;
    }
}
