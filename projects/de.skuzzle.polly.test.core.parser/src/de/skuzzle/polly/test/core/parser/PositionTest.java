package de.skuzzle.polly.test.core.parser;

import org.junit.Test;

import de.skuzzle.polly.core.parser.Position;

import junit.framework.Assert;


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
    
    
    
    @Test(expected = IllegalArgumentException.class)
    public void positionTest6() {
        new Position(1, 1);
    }
    
    
    
    @Test(expected = IllegalArgumentException.class)
    public void positionTest7() {
        new Position(1, 0);
    }
}
