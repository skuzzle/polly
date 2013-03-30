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
    
    
    
    @Test
    public void positionTest8() {
        final Position start = new Position(0, 1);
        final Position span = start.spanTo(new Position(3, 4));
        Assert.assertEquals(new Position(0, 4), span);
    }
    
    
    
    @Test
    public void positionTest9() {
        final Position end = new Position(3, 4);
        final Position span = end.spanFrom(new Position(0, 1));
        Assert.assertEquals(new Position(0, 4), span);
    }
    
    
    
    @Test
    public void positionTest10() {
        final Position pos = new Position(0, 1);
        Assert.assertEquals(new Position(3, 4), pos.offset(3));
    }
    
    
    
    @Test
    public void positionTest11() {
        final Position pos1 = new Position(0, 1);
        final Position pos2 = new Position(0, 1);
        Assert.assertEquals(0, pos1.compareTo(pos2));
    }
    
    
    
    @Test
    public void positionTest12() {
        final Position pos1 = new Position(0, 1);
        final Position pos2 = new Position(0, 2);
        Assert.assertTrue(pos1.compareTo(pos2) < 0);
        Assert.assertTrue(pos2.compareTo(pos1) >= 0);
    }
    
    
    
    @Test
    public void positionTest13() {
        final Position pos1 = new Position(0, 1);
        final Position pos2 = new Position(1, 2);
        Assert.assertTrue(pos1.compareTo(pos2) < 0);
        Assert.assertTrue(pos2.compareTo(pos1) >= 0);
    }
    
    
    
    @Test
    public void positionTest14() {
        final Position pos1 = new Position(2, 5);
        final Position pos2 = new Position(0, 2);
        Assert.assertTrue(pos1.compareTo(pos2) > 0);
        Assert.assertTrue(pos2.compareTo(pos1) <= 0);
    }
}
