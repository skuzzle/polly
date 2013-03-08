package de.skuzzle.test.polly.parsing.scanner;

import junit.framework.Assert;

import org.junit.Test;

import de.skuzzle.polly.core.parser.AbstractTokenStream;
import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Token;
import de.skuzzle.polly.core.parser.TokenType;

/**
 * Tests to read several numbers from a scanner.
 * 
 * @author Simon Taddiken
 */
public class TestReadNumbers extends AbstractScannerTest {

    @Test
    public final void testReadNumber1() throws ParseException {
        final String input = "1234";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();

        Assert.assertTrue(la.getFloatValue() == 1234);
    }
    
    
    
    @Test
    public final void testReadNumber2() throws ParseException {
        final String input = "0.1";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();

        Assert.assertTrue(Double.compare(la.getFloatValue(), 0.1) == 0);
    }
    
    
    
    @Test
    public final void testReadNumber3() throws ParseException {
        final String input = ".1";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();

        Assert.assertTrue(Double.compare(la.getFloatValue(), 0.1) == 0);
    }
    
    
    
    @Test
    public final void testReadNumber4() throws ParseException {
        final String input = ".1e2";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();

        Assert.assertTrue(Double.compare(la.getFloatValue(), 10.0) == 0);
    }
    
    
    
    @Test
    public final void testReadNumber5() throws ParseException {
        final String input = ".1e-2";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();

        Assert.assertTrue(Double.compare(la.getFloatValue(), 0.001) == 0);
    }
    
    
    
    @Test
    public final void testReadNumber6() throws ParseException {
        final String input = "10°";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();

        Assert.assertTrue(Double.compare(la.getFloatValue(), Math.toRadians(10.0)) == 0);
    }
    
    
    
    @Test
    public final void testReadNumber7() throws ParseException {
        final String input = "10:25";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        Assert.assertTrue(la.getType() == TokenType.DATETIME);
    }
    
    
    
    @Test
    public final void testReadNumber8() throws ParseException {
        final String input = "10:25@15.10.2013";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        Assert.assertTrue(la.getType() == TokenType.DATETIME);
    }
    
    
    
    @Test
    public final void testReadNumber9() throws ParseException {
        final String input = "5m10h";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        Assert.assertTrue(la.getType() == TokenType.TIMESPAN);
    }
    
    
    
    @Test(expected = ParseException.class)
    public final void testReadNumber10() throws ParseException {
        final String input = "5m10h5h";
        final AbstractTokenStream scanner = this.obtain(input);
        scanner.lookAhead();
    }
    
    
    
    @Test
    public final void testReadNumber11() throws ParseException {
        final String input = "5m10h4s3y1w";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        Assert.assertTrue(la.getType() == TokenType.TIMESPAN);
    }
}
