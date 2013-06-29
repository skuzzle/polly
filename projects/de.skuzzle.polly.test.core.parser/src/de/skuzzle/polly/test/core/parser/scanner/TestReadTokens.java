package de.skuzzle.polly.test.core.parser.scanner;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import de.skuzzle.polly.core.parser.AbstractTokenStream;
import de.skuzzle.polly.core.parser.EscapedToken;
import de.skuzzle.polly.core.parser.ParseException;
import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.Token;
import de.skuzzle.polly.core.parser.TokenType;

/**
 * Test cases that test several basic token scanning functions.
 * 
 * @author Simon Taddiken
 */
public class TestReadTokens extends AbstractScannerTest {

    @Test
    public void testReadTokens1() throws ParseException {
        final String input = "10 abc ";
        final AbstractTokenStream scanner = this.obtain(input);
        
        Assert.assertTrue(scanner.lookAhead().matches(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        Assert.assertTrue(scanner.lookAhead().matches(TokenType.IDENTIFIER));
        
        scanner.consume();
        
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        Assert.assertTrue(scanner.match(TokenType.EOS));
    }
    
    
    
    @Test
    public void testReadTokens2() throws ParseException {
        final String input = "10 abc ";
        final AbstractTokenStream scanner = this.obtain(input);
        
        Assert.assertTrue(scanner.match(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        
        scanner.pushBackFirst(new Token(TokenType.ADD, Position.NONE));
        
        Assert.assertTrue(scanner.match(TokenType.ADD));
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
    }
    
    
    
    @Test
    public void testReadTokens3() throws ParseException {
        final String input = "10 abc ";
        final AbstractTokenStream scanner = this.obtain(input);
        
        Assert.assertTrue(scanner.match(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        
        scanner.pushBackFirst(new Token(TokenType.ADD, Position.NONE));
        scanner.pushBackFirst(new Token(TokenType.SUB, Position.NONE));
        
        Assert.assertTrue(scanner.match(TokenType.SUB));
        Assert.assertTrue(scanner.match(TokenType.ADD));
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
    }
    
    
    
    @Test
    public void testReadTokens4() throws ParseException {
        final String input = "10 abc ";
        final AbstractTokenStream scanner = this.obtain(input);
        
        Assert.assertTrue(scanner.match(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        
        scanner.pushBackLast(new Token(TokenType.ADD, Position.NONE));
        scanner.pushBackLast(new Token(TokenType.SUB, Position.NONE));
        
        Assert.assertTrue(scanner.match(TokenType.ADD));
        Assert.assertTrue(scanner.match(TokenType.SUB));
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
    }
    
    
    
    @Test
    public void testReadTokens5() throws ParseException {
        final String input = "10 abc ";
        final AbstractTokenStream scanner = this.obtain(input);
        
        while (!scanner.match(TokenType.EOS)) {
            scanner.consume();
        }
        
        scanner.pushBackLast(new Token(TokenType.ADD, Position.NONE));
        scanner.pushBackLast(new Token(TokenType.SUB, Position.NONE));
        
        Assert.assertTrue(scanner.match(TokenType.ADD));
        Assert.assertTrue(scanner.match(TokenType.SUB));
        Assert.assertTrue(scanner.match(TokenType.EOS));
    }
    
    
    
    @Test
    public void testReadTokens6() throws ParseException {
        final String input = "10 abc ";
        final TokenType[] expected = {TokenType.NUMBER, TokenType.SEPERATOR, 
            TokenType.IDENTIFIER, TokenType.SEPERATOR, TokenType.EOS };
        
        final AbstractTokenStream scanner = this.obtain(input);
        
        int i = 0;
        final Iterator<Token> it = scanner.iterator();
        while (it.hasNext()) {
            final Token next = it.next();
            Assert.assertTrue(next.matches(expected[i++]));
        }
    }
    
    
    
    @Test
    public void testReadTokens7() throws ParseException {
        final String input = "10 abc #channel%/!!=~;&&& |||==<<<=>=>>+~+=-=??!\\(^^^$&|§";
        final AbstractTokenStream scanner = this.obtain(input);
        
        Assert.assertTrue(scanner.match(TokenType.NUMBER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        Assert.assertTrue(scanner.match(TokenType.IDENTIFIER));
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR));
        Assert.assertTrue(scanner.match(TokenType.CHANNEL));
        Assert.assertTrue(scanner.match(TokenType.MOD));
        Assert.assertTrue(scanner.match(TokenType.DIV));
        Assert.assertTrue(scanner.match(TokenType.EXCLAMATION));
        Assert.assertTrue(scanner.match(TokenType.NEQ));
        Assert.assertTrue(scanner.match(TokenType.WAVE));
        Assert.assertTrue(scanner.match(TokenType.SEMICOLON));
        Assert.assertTrue(scanner.match(TokenType.BOOLEAN_AND));
        Assert.assertTrue(scanner.match(TokenType.INT_AND));
        //HACK: and or operator interference  
        Assert.assertTrue(scanner.match(TokenType.SEPERATOR)); 
        Assert.assertTrue(scanner.match(TokenType.BOOLEAN_OR));
        Assert.assertTrue(scanner.match(TokenType.INT_OR));
        Assert.assertTrue(scanner.match(TokenType.EQ));
        Assert.assertTrue(scanner.match(TokenType.LEFT_SHIFT));
        Assert.assertTrue(scanner.match(TokenType.ELT));
        Assert.assertTrue(scanner.match(TokenType.EGT));
        Assert.assertTrue(scanner.match(TokenType.GT));
        Assert.assertTrue(scanner.match(TokenType.GT));
        Assert.assertTrue(scanner.match(TokenType.ADDWAVE));
        Assert.assertTrue(scanner.match(TokenType.ADDEQUALS));
        Assert.assertTrue(scanner.match(TokenType.SUBEQUALS));
        Assert.assertTrue(scanner.match(TokenType.QUESTION));
        Assert.assertTrue(scanner.match(TokenType.QUEST_EXCALAMTION));
        Assert.assertTrue(scanner.match(TokenType.LAMBDA));
        Assert.assertTrue(scanner.match(TokenType.XOR));
        Assert.assertTrue(scanner.match(TokenType.POWER));
        Assert.assertTrue(scanner.match(TokenType.DOLLAR));
        Assert.assertTrue(scanner.match(TokenType.AND_OR));
        Assert.assertTrue(scanner.match(TokenType.ERROR));
    }
    
    
    
    @Test
    public void testReadTokens8() throws ParseException {
        final String input = "=7";
        final AbstractTokenStream scanner = this.obtain(input);
        
        Assert.assertTrue(scanner.match(TokenType.ERROR));
    }
    
    
    
    @Test
    public void testReadTokens9() throws ParseException {
        final String input = "\\#escaped";
        final AbstractTokenStream scanner = this.obtain(input);
        final Token la = scanner.consume();
        Assert.assertTrue(la.matches(TokenType.ESCAPED));
        
        final EscapedToken escaped = (EscapedToken) la;
        Assert.assertTrue(escaped.getEscaped().matches(TokenType.CHANNEL));
    }
}

