package de.skuzzle.polly.tools.math;

/**
 * Represents an immutable pair of objects.
 * 
 * @author Simon
 *
 * @param <T1> Type of first argument of this pair.
 * @param <T2> Type of second argument of this pair.
 */
public class Tupel2<T1, T2> implements Cloneable {
    
    private final T1 first;
    private final T2 second;
    
    
    
    /**
     * Constructs a pair with two given objects.
     * 
     * @param first First object of this pair.
     * @param second Second object of this pair.
     */
    public Tupel2(T1 first, T2 second) {
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
    public Tupel2(Tupel2<T1, T2> other) {
        this.first = other.getFirst();
        this.second = other.getSecond();
    }


    
    public T1 getFirst() {
        return first;
    }
    

    public T2 getSecond() {
        return second;
    }
    
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((this.first == null) ? 0 : this.first.hashCode());
        result = prime * result
            + ((this.second == null) ? 0 : this.second.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tupel2)) {
            return false;
        }
        Tupel2<?, ?> other = (Tupel2<?, ?>) obj;
        if (this.first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!this.first.equals(other.first)) {
            return false;
        }
        if (this.second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!this.second.equals(other.second)) {
            return false;
        }
        return true;
    }



    @Override
    public Object clone() {
        return new Tupel2<T1, T2>(this);
    }
}
