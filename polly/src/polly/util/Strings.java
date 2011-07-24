package polly.util;

import java.util.Collection;

import polly.util.Functional.BinaryOperation;


public class Strings {

    public static <B> String toStringList(Collection<B> c, String seperator) {
        BinaryOperation<String, String, B> op = new BinaryOperation<String, String, B>() {
            
            @Override
            public String execute(String p1, B p2) {
                return p2.toString().concat(p1);
            }
        };
        
        return Functional.foldLeft(c, seperator, op);
    }
    
    
    public static <T> String toStringList(Collection<T> list, char seperator) {
        return Strings.toStringList(list, Character.toString(seperator));
    }
    
    
    
    public static String padSpaces(int count) {
        StringBuilder b = new StringBuilder(count);
        Strings.padSpaces(b, count);
        return b.toString();
    }
    
    
    public static void padSpaces(StringBuilder b, int count) {
        for (int i = 0; i < count; ++i) {
            b.append(" ");
        }
    }
}
