package de.skuzzle.test.polly.parsing.scanner;

import org.junit.Test;

import junit.framework.Assert;
import de.skuzzle.polly.parsing.AbstractTokenStream;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;


public class TestPositioning extends AbstractScannerTest {

    @Test
    public void testPositioning1() throws ParseException {
        final String input = "a abc";
        final AbstractTokenStream scanner = this.obtain(input);
        Assert.assertEquals(scanner.consume().getPosition(), new Position(0, 1));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(1, 2));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(2, 5));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(5, 6));
    }
    
    
    
    @Test
    public void testPositioning2() throws ParseException {
        final String input = "1.45 abc @bla";
        final AbstractTokenStream scanner = this.obtain(input);
        Assert.assertEquals(scanner.consume().getPosition(), new Position(0, 4));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(4, 5));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(5, 8));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(8, 9));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(9, 13));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(13, 14));
    }
    
    
    
    @Test
    public void testPositioning3() throws ParseException {
        final String input = "()>10m4h3s";
        final AbstractTokenStream scanner = this.obtain(input);
        Assert.assertEquals(scanner.consume().getPosition(), new Position(0, 1));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(1, 2));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(2, 3));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(3, 10));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(10, 11));
    }
    
    
    
    @Test
    public void testPositioning4() throws ParseException {
        final String input = "longid";
        final AbstractTokenStream scanner = this.obtain(input);
        Assert.assertEquals(scanner.consume().getPosition(), new Position(0, 6));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(6, 7));
    }
    
    
    
    @Test
    public void testPositioning5() throws ParseException {
        final String input = "10m4h 09.12.1987@13:37";
        final AbstractTokenStream scanner = this.obtain(input);
        Assert.assertEquals(scanner.consume().getPosition(), new Position(0, 5));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(5, 6));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(6, 22));
        Assert.assertEquals(scanner.consume().getPosition(), new Position(22, 23));
    }
}