package de.skuzzle.test.polly.parsing.scanner;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import de.skuzzle.polly.core.parser.AbstractTokenStream;
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
            Assert.assertTrue(it.next().matches(expected[i++]));
        }
    }
}
