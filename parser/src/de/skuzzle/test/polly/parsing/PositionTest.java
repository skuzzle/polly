package de.skuzzle.test.polly.parsing;

import org.junit.Test;

import junit.framework.Assert;
import de.skuzzle.polly.parsing.Position;


public class PositionTest {

    @Test
    public void positionTest1() {
        final Position pos1 = new Position(1, 4);
        final Position pos2 = new Position(3, 6);
        Assert.assertTrue(pos1.overlap(pos2));
        Assert.assertTrue(pos2.overlap(pos1));
    }
    
    
    
    @Test
    public void positionTest2() {
        final Position pos1 = new Position(1, 4);
        Assert.assertTrue(pos1.overlap(pos1));
    }
    
    
    
    @Test
    public void positionTest3() {
        final Position pos1 = new Position(1, 4);
        final Position pos2 = new Position(4, 6);
        Assert.assertFalse(pos1.overlap(pos2));
        Assert.assertFalse(pos2.overlap(pos1));
    }
    
    
    
    @Test
    public void positionTest4() {
        final Position pos1 = new Position(1, 4);
        final Position pos2 = new Position(1, 4);
        Assert.assertEquals(pos1, pos2);
    }
    
    
    
    @Test
    public void positionTest5() {
        final Position pos1 = new Position(1, 4);
        final Position pos2 = new Position(2, 4);
        Assert.assertFalse(pos1.equals(pos2));
    }
}
