package de.skuzzle.test.polly.parsing;
/*
import org.junit.Assert;
import org.junit.Test;

import de.skuzzle.polly.parsing.InputScanner;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.TokenType;



public class InputScannerTest {

    @Test
    public void testIdentifier() throws ParseException {
        String test = "test";
        InputScanner s = new InputScanner(test);
        Assert.assertTrue(test + " no identifier", 
                s.lookAhead().getType() == TokenType.IDENTIFIER);
        Assert.assertEquals(test + " does not equal tokens Stringvalue", test,
                s.lookAhead().getStringValue());
        
        test = "_test";
        s = new InputScanner(test);
        Assert.assertTrue(test + " no identifier", 
                s.lookAhead().getType() == TokenType.IDENTIFIER);
        Assert.assertEquals(test + " does not equal tokens Stringvalue", test,
                s.lookAhead().getStringValue());
        
        test = "_12test";
        s = new InputScanner(test);
        Assert.assertTrue(test + " no identifier", 
                s.lookAhead().getType() == TokenType.IDENTIFIER);
        Assert.assertEquals(test + " does not equal tokens Stringvalue", test,
                s.lookAhead().getStringValue());
    }
    
    
    
    @Test
    public void testChannelLiteral() throws ParseException {
        String test = "#test";
        InputScanner s = new InputScanner(test);
        Assert.assertTrue(test + " no channel literal", 
                s.lookAhead().getType() == TokenType.CHANNEL);
        Assert.assertEquals(test + " does not equal tokens Stringvalue", test,
                s.lookAhead().getStringValue());
    }
    
    
    
    @Test
    public void test2CharSymbols() throws ParseException {
        String test = "!=";
        InputScanner s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.NEQ);
        
        test = "!";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.EXCLAMATION);

        test = "+";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.ADD);
        
        test = "+~";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.ADDWAVE);
        
        test = "~";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.WAVE);
        
        test = "-";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.SUB);
        
        test = "->";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.ASSIGNMENT);
        
        test = "<=";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.ELT);
        
        test = ">=";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.EGT);
        
        test = "..";
        s = new InputScanner(test);
        Assert.assertTrue(s.lookAhead().getType() == TokenType.DOTDOT);
    }
    
    
    @Test
    public void testNumber() throws ParseException {
        String test = "1.058";
        double t = 1.058d;
        InputScanner s = new InputScanner(test);
        Assert.assertTrue(test + " no number literal", 
                s.lookAhead().getType() == TokenType.NUMBER);
        Assert.assertTrue(test + " does not equal tokens floatvalue " + t, 
                t == s.lookAhead().getFloatValue());
        
        test = "10.58e-5";
        t = 10.58e-5d;
        s = new InputScanner(test);
        Assert.assertTrue(test + " no number literal", 
                s.lookAhead().getType() == TokenType.NUMBER);
        Assert.assertTrue(test + " does not equal tokens floatvalue " + t, 
                t == s.lookAhead().getFloatValue());
        
        test = "10E3";
        t = 10E3d;
        s = new InputScanner(test);
        Assert.assertTrue(test + " no number literal", 
                s.lookAhead().getType() == TokenType.NUMBER);
        Assert.assertTrue(test + " does not equal tokens floatvalue " + t, 
                t == s.lookAhead().getFloatValue());
        
        test = "10E3°";
        t = 10E3d;
        s = new InputScanner(test);
        Assert.assertTrue(test + " no number literal", 
                s.lookAhead().getType() == TokenType.NUMBER);
        
        test = "12:57";
        s = new InputScanner(test);
        Assert.assertTrue(test + " no date literal", 
                s.lookAhead().getType() == TokenType.DATETIME);
        
        test = "27.3.2007@12:57";
        s = new InputScanner(test);
        Assert.assertTrue(test + " no date literal", 
                s.lookAhead().getType() == TokenType.DATETIME);
        
        test = "05.03.2011";
        s = new InputScanner(test);
        Assert.assertTrue(test + " no date literal", 
                s.lookAhead().getType() == TokenType.DATETIME);
    }
    
    
    
    @Test(expected = ParseException.class)
    public void testInvalidNumber1() throws ParseException {
        String test = "1.";
        InputScanner s = new InputScanner(test);
        s.lookAhead();
    }
    
    
    
    @Test(expected = ParseException.class)
    public void testInvalidNumber2() throws ParseException {
        String test = "1.5.";
        InputScanner s = new InputScanner(test);
        s.lookAhead();
    }
    
    
    
    @Test
    public void testComplex() throws ParseException {
        String test = "1.5..17.8";
        InputScanner s = new InputScanner(test);
        s.match(TokenType.NUMBER);
        s.match(TokenType.DOTDOT);
        s.match(TokenType.NUMBER);
        s.match(TokenType.EOS);

    }
}
*/