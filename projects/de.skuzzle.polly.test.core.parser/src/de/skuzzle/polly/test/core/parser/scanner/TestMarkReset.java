package de.skuzzle.polly.test.core.parser.scanner;

import junit.framework.Assert;

import org.junit.Test;

import de.skuzzle.polly.core.parser.AbstractTokenStream;
import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Token;
import de.skuzzle.polly.core.parser.TokenType;



/**
 * Test cases that test the functionality of mark()/reset() function of the 
 * {@link AbstractTokenStream}.
 *  
 * @author Simon Taddiken
 */
public class TestMarkReset extends AbstractScannerTest {

    @Test
    public final void testMark1() throws ParseException {
        final String input = ":foo";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.mark();
        scanner.consume();
        scanner.reset(false, false);
        Assert.assertTrue(scanner.match(TokenType.COLON));
    }
    
    
    
    @Test
    public final void testMark2() throws ParseException {
        final String input = ":foo";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.mark();
        scanner.lookAhead();
        scanner.reset(false, false);
        Assert.assertTrue(scanner.match(TokenType.COLON));
    }
    
    
    
    @Test
    public final void testMark3() throws ParseException {
        final String input = ":foo";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.mark();
        scanner.consume();
        scanner.lookAhead();
        scanner.reset(false, false);
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
    }
    
    
    
    @Test
    public final void testMark4() throws ParseException {
        final String input = ":foo";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.mark();
        scanner.consume();
        scanner.lookAhead();
        scanner.reset(false, true);
        Assert.assertTrue(scanner.match(TokenType.COLON));
    }
    
    
    
    @Test(expected = IllegalStateException.class)
    public final void testMark5() throws ParseException {
        final String input = ":foo";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.reset(false, false);
    }
    
    
    
    @Test
    public final void testMark6() throws ParseException {
        final String input = ":foo 1 \"test\"";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.mark();
        while (!scanner.match(TokenType.EOS)) {
            scanner.consume();
        }
        scanner.reset(false, false);
        Assert.assertTrue(scanner.match(TokenType.COLON));
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        Assert.assertTrue(scanner.match(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        Assert.assertTrue(scanner.match(TokenType.STRING));
        Assert.assertTrue(scanner.match(TokenType.EOS));
        scanner.reset(false, false);
        Assert.assertTrue(scanner.match(TokenType.COLON));
    }
    
    
    
    @Test
    public final void testMark7() throws ParseException {
        final String input = ":foo 1 \"test\"";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        scanner.consume();
        scanner.mark();
        scanner.pushBackFirst(la);
        scanner.reset(false, false);
        Assert.assertTrue(scanner.match(TokenType.COLON));
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
    }
    
    
    
    @Test
    public final void testMark8() throws ParseException {
        final String input = ":foo 1 \"test\"";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        scanner.consume();
        scanner.mark();
        scanner.pushBackFirst(la);
        scanner.reset(false, true);
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
    }
}
