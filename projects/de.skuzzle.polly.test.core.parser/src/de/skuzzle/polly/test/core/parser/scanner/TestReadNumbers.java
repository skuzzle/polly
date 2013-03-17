package de.skuzzle.polly.test.core.parser.scanner;

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
    
    
    
    @Test
    public final void testReadNumber10() throws ParseException {
        final String input = "5m10h5h";
        final AbstractTokenStream scanner = this.obtain(input);
        Assert.assertTrue(scanner.match(TokenType.ERROR));
    }
    
    
    
    @Test
    public final void testReadNumber11() throws ParseException {
        final String input = "5m10h4s3y1w";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.lookAhead();
        Assert.assertTrue(la.getType() == TokenType.TIMESPAN);
    }
    
    
    
    @Test
    public final void testReadNumber12() throws ParseException {
        final String input = "1e3 1E3 1.23e3";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertTrue(Double.compare(la.getFloatValue(), 1000.0) == 0);
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertTrue(Double.compare(la.getFloatValue(), 1000.0) == 0);
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertTrue(Double.compare(la.getFloatValue(), 1230.0) == 0);
    }
    
    
    
    @Test
    public final void testReadNumber13() throws ParseException {
        final String input = "1.5..2.5";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertTrue(la.getFloatValue() == 1.5);
        Assert.assertTrue(scanner.match(TokenType.DOTDOT));
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertTrue(la.getFloatValue() == 2.5);
    }
    
    
    
    @Test
    public final void testReadNumber14() throws ParseException {
        final String input = "1.";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber15() throws ParseException {
        final String input = "0x";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber16() throws ParseException {
        final String input = "0x44:";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber17() throws ParseException {
        final String input = "0x44";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
}
