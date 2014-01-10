package de.skuzzle.polly.tools;


public final class Check {

    public static void notNull(Object o, String msg) {
        if (o == null) {
            throw new NullPointerException(msg);
        }
    }
    
    
    
    public static void notNull(Object...o) {
        if (o == null) {
            throw new NullPointerException();
        }
        for (final Object obj : o) {
            notNull(obj, "");
        }
    }
}
