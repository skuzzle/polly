package de.skuzzle.test.polly.parsing.scanner;


import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import de.skuzzle.polly.core.parser.InputScanner;

/**
 * Tests some basic character reading.
 * 
 * @author Simon Taddiken
 */
public class TestReadCharacters extends AbstractScannerTest {
    
    /** Changes visibility of character reading methods */
    @Ignore
    private final class PublicTokenStream extends InputScanner {
        
        public PublicTokenStream(String input) {
            super(input);
        }
        
        
        @Override
        public boolean nextIs(int c) {
            return super.nextIs(c);
        }
        
        
        
        @Override
        public int readChar() {
            return super.readChar();
        }
        
        
        
        @Override
        public void pushBack(int t) {
            super.pushBack(t);
        }
    }
    
    
    
    @Override
    protected PublicTokenStream obtain(String input) {
        return new PublicTokenStream(input);
    }
    
    

    @Test
    public void testReadCharacters1() {
        final String input = "1234";
        final PublicTokenStream scanner = this.obtain(input);
        Assert.assertTrue(scanner.nextIs('1'));
        Assert.assertTrue(scanner.nextIs('2'));
        Assert.assertFalse(scanner.nextIs('4'));
        Assert.assertTrue(scanner.nextIs('3'));
        Assert.assertTrue(scanner.nextIs('4'));
        Assert.assertTrue(scanner.nextIs(-1));
    }
    
    
    
    @Test
    public void testReadCharacters2() {
        final String input = "1234";
        final PublicTokenStream scanner = this.obtain(input);
        Assert.assertTrue(scanner.nextIs('1'));
        scanner.pushBack('5');
        scanner.pushBack('6');
        Assert.assertTrue(scanner.nextIs('5'));
        Assert.assertTrue(scanner.nextIs('6'));
        Assert.assertTrue(scanner.nextIs('2'));
        Assert.assertTrue(scanner.nextIs('3'));
        Assert.assertTrue(scanner.nextIs('4'));
        Assert.assertTrue(scanner.nextIs(-1));
    }
    
    
    
    @Test
    public void testReadCharacters3() {
        final String input = "";
        final PublicTokenStream scanner = this.obtain(input);
        Assert.assertTrue(scanner.nextIs(-1));
    }
    
    
    
    @Test
    public void testReadCharacters4() {
        final String input = "";
        final PublicTokenStream scanner = this.obtain(input);
        scanner.pushBack('a');
        Assert.assertTrue(scanner.nextIs('a'));
        Assert.assertTrue(scanner.nextIs(-1));
    }
}
