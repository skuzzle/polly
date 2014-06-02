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
        final String input = "1e3 1E3 1.23e3 1.23e-3 1.23e+3 1.23e+ 1.23e- 1.23e";
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
        
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertEquals(1.23E-3, la.getFloatValue());
        
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertEquals(1.23E+3, la.getFloatValue());
        
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
        
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
        
        scanner.consume();
        la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
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
    
    
    
    @Test
    public final void testReadNumber18() throws ParseException {
        final String input = "2#1001";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertEquals(9.0, la.getFloatValue());
    }
    
    
    
    @Test
    public final void testReadNumber19() throws ParseException {
        final String input = "44#1001";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber20() throws ParseException {
        final String input = "2#a";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber21() throws ParseException {
        final String input = "2#1a";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertEquals(1.0, la.getFloatValue());
    }
    
    
    
    @Test
    public final void testReadNumber22() throws ParseException {
        final String input = "24:15";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber23() throws ParseException {
        final String input = "23:15";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.DATETIME);
    }
    
    
    
    @Test
    public final void testReadNumber24() throws ParseException {
        final String input = "20°";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertEquals(Math.toRadians(20.0), la.getFloatValue());
    }
    
    
    
    @Test
    public final void testReadNumber25() throws ParseException {
        final String input = "20";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.NUMBER);
        Assert.assertEquals(20.0, la.getFloatValue());
    }
    
    
    
    @Test
    public final void testReadNumber26() throws ParseException {
        final String input = "10.12.1987@13:37";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.DATETIME);
    }
    
    
    
    @Test
    public final void testReadNumber27() throws ParseException {
        final String input = "32.12.1987@13:37";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber28() throws ParseException {
        final String input = "10.13.1987@13:37";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber29() throws ParseException {
        final String input = "10.12.1987@13";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber31() throws ParseException {
        final String input = "10.12.10000";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber32() throws ParseException {
        final String input = "10.12.1987@13:";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber33() throws ParseException {
        final String input = "10.12.1987@a";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
    
    
    
    @Test
    public final void testReadNumber34() throws ParseException {
        final String input = "10.12.1987@";
        final AbstractTokenStream scanner = this.obtain(input);
        Token la = scanner.consume();
        Assert.assertTrue(la.getType() == TokenType.ERROR);
    }
}
