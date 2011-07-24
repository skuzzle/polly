package polly.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;



/**
 * <p>This class provides an iterator interface to wrap a String into multiple lines with
 * desired max line length.</p>
 * 
 * <p>You can iterate over the wrapped lines using Javas for...each feature:</p>
 * <pre>
 *      String s = "Very long long long String to wrap into several lines, where no " +
 *                 "line is longer than the amount of characters you specify in the " +
 *                 "WrapIterator's constructor. In our sample we use 10 characters.";
 *                 
 *      for (String wrappedLine : new WrapIterator(s, 10)) {
 *          System.out.println(wrappedLine);
 *      }
 * </pre>
 * 
 * @author Simon
 *
 */
public class WrapIterator implements Iterator<String>, Iterable<String> {

    public static void main(String[] args) {
        String s = "Very long long long String to wrap into several lines, where no " +
                   "line is longer than the amount of characters you specify in the " +
                   "WrapIterator's constructor. In our sample we use 15 characters. " + 
                   "NowWeNeedAWordWithMoreThan15Characters. AnotherVeryLongRotzWord";
                             
        /*for (String wrappedLine : WrapIterator.get(s, 25)) {
            System.out.println(wrappedLine);
        }*/
        
        
        System.out.println(wrapIntoString(s, 25));
        wrapIntoString(s, 25);
    }
    
    
    
    public static Iterable<String> get(String s, int lineLength) {
        return new WrapIterator(s, lineLength);
    }
    
    
    
    public static String wrapIntoString(String s, int lineLength) {
        StringBuilder b = new StringBuilder(s.length() + 50);
        Iterator<String> it = new WrapIterator(s, lineLength);

        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append("\n");
            }
        }
        return b.toString();
    }
    
    
    
    public static String[] wrapIntoArray(String s, int lineLength) {
        return wrapIntoList(s, lineLength).toArray(new String[0]);
    }
    
    
    
    public static List<String> wrapIntoList(String s, int lineLength) {
        ArrayList<String> result = new ArrayList<String>();
        for (String line : new WrapIterator(s, lineLength)) {
            result.add(line);
        }
        return result;
    }
    
    
    
    private ArrayIterator<String> words;
    private int length;
    
    
    
    public WrapIterator(String string, int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Linelength must be at least 1 char");
        }
        this.length = length;
        this.words = ArrayIterator.get(string.split(" "));
    }

    
    
    @Override
    public boolean hasNext() {
        return this.words.hasNext();
    }
    
    

    @Override
    public String next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        
        StringBuilder b = new StringBuilder(this.length);
        
        while (this.words.hasNext()) {
            String word = this.words.next();
            int i = this.words.hasNext() ? 1 : 0;
            
            if (b.length() + word.length() + i <= this.length) {
                b.append(word);
                if (this.words.hasNext()) {
                    // only append space when this is not the last word on this line
                    b.append(" ");
                }
            } else {
                if (word.length() >= this.length) {
                    // This word is longer than the max length. So it's cut into two
                    // pieces: the first piece gets the length to fit on the current
                    // line, the second piece will start the next line.
                    int free = this.length - b.length();
                    String right = word.substring(free);
                    if (right.length() > 0) {
                        this.words.replace(word.substring(free));
                        this.words.previous();
                    }
                    b.append(word.substring(0, free));
                } else {
                    // The current word does not fit on the current line. So iteration
                    // is moved one step back so the word will start the next line.
                    this.words.previous();
                }

                return b.toString();
            }
        }
        return b.toString();
    }
       
    

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }



    @Override
    public Iterator<String> iterator() {
        return this;
    }
}