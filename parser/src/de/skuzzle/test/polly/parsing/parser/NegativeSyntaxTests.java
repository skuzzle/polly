package de.skuzzle.test.polly.parsing.parser;

import junit.framework.Assert;

import org.junit.Test;

import de.skuzzle.polly.parsing.InputParser;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.SyntaxException;
import de.skuzzle.polly.parsing.TokenType;

/**
 * Tests whether syntactical incorrect inputs are considered wrong.
 * 
 * @author Simon Taddiken
 */
public class NegativeSyntaxTests extends AbstractParserTest {
    
    @Test
    public void parseTest1() throws ParseException {
        final String input = ":foo (5+5";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.CLOSEDBR, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest2() throws ParseException {
        final String input = ":foo {1,2";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.CLOSEDCURLBR, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest3() throws ParseException {
        final String input = ":foo 5+";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.LITERAL, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest4() throws ParseException {
        final String input = ":foo a[b";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.CLOSEDSQBR, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest5() throws ParseException {
        final String input = ":foo \\(list<num b: a)";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.GT, e.getExpected());
            Assert.assertEquals(TokenType.IDENTIFIER, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest6() throws ParseException {
        final String input = ":foo \\( (num): a)";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.ASSIGNMENT, e.getExpected());
            Assert.assertEquals(TokenType.CLOSEDBR, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest7() throws ParseException {
        final String input = ":foo \\( (num->): a)";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.IDENTIFIER, e.getExpected());
            Assert.assertEquals(TokenType.CLOSEDBR, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest8() throws ParseException {
        final String input = ":foo \\( num a)";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.COLON, e.getExpected());
            Assert.assertEquals(TokenType.CLOSEDBR, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest9() throws ParseException {
        final String input = ":foo if 1<2";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.COLON, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest10() throws ParseException {
        final String input = ":foo if 1<2 : ";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.LITERAL, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
    
    
    
    @Test
    public void parseTest11() throws ParseException {
        final String input = ":foo if 1<2 : 5";
        final InputParser parser = this.obtain(input);
        try {
            parser.parse();
            Assert.fail();
        } catch (SyntaxException e) {
            Assert.assertEquals(TokenType.COLON, e.getExpected());
            Assert.assertEquals(TokenType.EOS, e.getFound().getType());
        }
    }
}
