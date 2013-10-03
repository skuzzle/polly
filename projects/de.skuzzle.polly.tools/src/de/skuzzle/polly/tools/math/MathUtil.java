package de.skuzzle.polly.tools.math;


public final class MathUtil {

    public final static int limit(int num, int bound1, int bound2) {
        final int lowerBound = Math.min(bound1, bound2);
        final int upperBound = Math.max(bound1, bound2);
        return Math.max(lowerBound, Math.min(upperBound, num));
    }
    
    
    
    public final static double limit(double num, double bound1, double bound2) {
        final double lowerBound = Math.min(bound1, bound2);
        final double upperBound = Math.max(bound1, bound2);
        return Math.max(lowerBound, Math.min(upperBound, num));
    }
    
    
    
    public final static <T extends Comparable<T>> T limit(T elem, T bound1, T bound2) {
        final T lowerBound = min(bound1, bound2);
        final T upperBound = max(bound1, bound2);
        return max(lowerBound, min(upperBound, elem));
    }
    
    
    
    public final static <T extends Comparable<T>> T max(T e1, T e2) {
        final int comp = e1.compareTo(e2);
        if (comp < 0) {
            return e2;
        } else {
            return e1;
        }
    }
    
    
    
    public final static <T extends Comparable<T>> T min(T e1, T e2) {
        final int comp = e1.compareTo(e2);
        if (comp < 0) {
            return e2;
        } else {
            return e1;
        }
    }
    
    
    
    public final static <T extends Comparable<T>> boolean between(
            T elem, T bound1, T bound2) {
        
        final T lowerBound = min(bound1, bound2);
        final T upperBound = max(bound1, bound2);
        
        return (elem.compareTo(lowerBound) >= 0) && (elem.compareTo(upperBound) <= 0);
    }
    
    
    
    public final static boolean between(int num, int bound1, int bound2) {
        final int lowerBound = Math.min(bound1, bound2);
        final int upperBound = Math.max(bound1, bound2);
        return num >= lowerBound && num <= upperBound;
    }
    
    
    
    public final static boolean between(double num, double bound1, double bound2) {
        final double lowerBound = Math.min(bound1, bound2);
        final double upperBound = Math.max(bound1, bound2);
        return num >= lowerBound && num <= upperBound;
    }
    
    
    
    private MathUtil() {}
}
