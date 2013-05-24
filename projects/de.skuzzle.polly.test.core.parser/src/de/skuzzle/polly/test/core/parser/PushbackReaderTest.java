package de.skuzzle.polly.test.core.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;

import de.skuzzle.polly.core.parser.PushbackReader;


public class PushbackReaderTest {
    
    private PushbackReader obtain(String input) {
        return new PushbackReader(new InputStreamReader(
            new ByteArrayInputStream(input.getBytes())));
    }
    
    

    @Test
    public void testReader1() throws IOException {
        final String input = "abc";
        final PushbackReader reader = this.obtain(input);
        
        Assert.assertEquals(0, reader.getPosition());
        Assert.assertEquals('a', reader.read());
        
        Assert.assertEquals(1, reader.getPosition());
        Assert.assertEquals('b', reader.read());
        
        Assert.assertEquals(2, reader.getPosition());
        Assert.assertEquals('c', reader.read());
        
        Assert.assertEquals(3, reader.getPosition());
        Assert.assertEquals(-1, reader.read());
        Assert.assertTrue(reader.eos());
        
        Assert.assertEquals(-1, reader.read());
        Assert.assertEquals(3, reader.getPosition());
        Assert.assertTrue(reader.eos());
    }
    
    
    
    @Test
    public void testReader2() throws IOException {
        final String input = "abc";
        final PushbackReader reader = this.obtain(input);
        
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(1, reader.getPosition());
        
        reader.pushback('a');
        Assert.assertEquals(0, reader.getPosition());
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(1, reader.getPosition());

        Assert.assertEquals('b', reader.read());
        Assert.assertEquals('c', reader.read());
        Assert.assertEquals(-1, reader.read());
        Assert.assertEquals(3, reader.getPosition());
    }
    
    
    
    @Test
    public void testReader3() throws IOException {
        final String input = "abc";
        final PushbackReader reader = this.obtain(input);
        
        reader.pushbackInvisible('d');
        Assert.assertEquals(0, reader.getPosition());
        Assert.assertEquals('d', reader.read());
        Assert.assertEquals(0, reader.getPosition());
        
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(1, reader.getPosition());

        Assert.assertEquals('b', reader.read());
        Assert.assertEquals(2, reader.getPosition());
    }
    
    
    
    @Test
    public void testReader4() throws IOException {
        final String input = "abc";
        final PushbackReader reader = this.obtain(input);
        
        reader.pushbackInvisible('d');
        reader.pushbackInvisible('e');
        Assert.assertEquals(0, reader.getPosition());
        Assert.assertEquals('d', reader.read());
        Assert.assertEquals(0, reader.getPosition());
        Assert.assertEquals('e', reader.read());
        Assert.assertEquals(0, reader.getPosition());
        
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(1, reader.getPosition());

        Assert.assertEquals('b', reader.read());
        Assert.assertEquals(2, reader.getPosition());
    }
    
    
    
    @Test
    public void testReader5() throws IOException {
        final String input = "abc";
        final PushbackReader reader = this.obtain(input);
        
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals('b', reader.read());
        Assert.assertEquals('c', reader.read());
        Assert.assertEquals(-1, reader.read());
        Assert.assertTrue(reader.eos());
        reader.pushbackInvisible('d');
        Assert.assertFalse(reader.eos());
        Assert.assertEquals(3, reader.getPosition());
    }
    
    
    
    @Test
    public void testReader8() throws IOException {
        final String input = "a";
        final PushbackReader reader = this.obtain(input);
        reader.read();
        reader.read();
        Assert.assertTrue(reader.eos());
        
        reader.pushbackInvisible('a');
        Assert.assertFalse(reader.eos());
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(-1, reader.read());
        Assert.assertTrue(reader.eos());
    }
    
    
    
    @Test
    public void testReader9() throws IOException {
        final String input = "a";
        final PushbackReader reader = this.obtain(input);
        reader.pushbackInvisible('b');
        reader.read();
        reader.read();
        reader.read();
        Assert.assertTrue(reader.eos());
    }
    
    
    
    @Test
    public void testReader10() throws IOException {
        final String input = "";
        final PushbackReader reader = this.obtain(input);
        Assert.assertEquals(-1, reader.read());
        Assert.assertTrue(reader.eos());
    }
    
    
    
    @Test
    public void testReader11() throws IOException {
        final String input = "";
        final PushbackReader reader = this.obtain(input);
        reader.pushbackInvisible('a');
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(-1, reader.read());
        Assert.assertTrue(reader.eos());
    }
    
    
    
    @Test
    public void testReader12() throws IOException {
        final String input = "a\nb\rc";
        final PushbackReader reader = this.obtain(input);
        Assert.assertEquals(0, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals(1, reader.getCol());
        
        Assert.assertEquals('\n', reader.read());
        Assert.assertEquals(1, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        Assert.assertEquals(1, reader.getCols(0));
        
        Assert.assertEquals('b', reader.read());
        Assert.assertEquals(1, reader.getCol());
        
        Assert.assertEquals('\n', reader.read());
        Assert.assertEquals(2, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        Assert.assertEquals(1, reader.getCols(1));
        
        Assert.assertEquals('c', reader.read());
        
        Assert.assertEquals(-1, reader.read());
        Assert.assertTrue(reader.eos());
    }
    
    
    
    @Test
    public void testReader13() throws IOException {
        final String input = "\n";
        final PushbackReader reader = this.obtain(input);
        int line = reader.getLineNumber();
        int col = reader.getCol();
        int c = reader.read();
        int lineAfter = reader.getLineNumber();
        int colAfter = reader.getCol();
        
        reader.pushback(c);
        Assert.assertEquals(line, reader.getLineNumber());
        Assert.assertEquals(col, reader.getCol());
        
        Assert.assertEquals(c, reader.read());
        Assert.assertEquals(lineAfter, reader.getLineNumber());
        Assert.assertEquals(colAfter, reader.getCol());
        Assert.assertEquals(0, reader.getCols(0));
    }
    
    
    
    @Test
    public void testReader14() throws IOException {
        final String input = "a\ncd";
        final PushbackReader reader = this.obtain(input);
        reader.pushbackInvisible('b');
        Assert.assertEquals(0, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        
        int c = reader.read();
        Assert.assertEquals(c, 'b');
        Assert.assertEquals(0, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals('\n', reader.read());
        Assert.assertEquals(1, reader.getLineNumber());
        
        reader.pushbackInvisible('\n');
        Assert.assertEquals('\n', reader.read());
        Assert.assertEquals(1, reader.getLineNumber());
        Assert.assertEquals('c', reader.read());
        Assert.assertEquals(1, reader.getLineNumber());
        Assert.assertEquals(1, reader.getCol());
        Assert.assertEquals('d', reader.read());
        Assert.assertEquals(-1, reader.read());
        
        Assert.assertEquals(1, reader.getCols(0));
        Assert.assertEquals(2, reader.getCols(1));
    }
    
    
    
    @Test
    public void testReader15() throws IOException {
        final String input = "abcd\ncd";
        final PushbackReader reader = this.obtain(input);
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals('b', reader.read());
        Assert.assertEquals('c', reader.read());
        Assert.assertEquals('d', reader.read());
        Assert.assertEquals('\n', reader.read());
        Assert.assertEquals(1, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        
        reader.pushback('\n');
        Assert.assertEquals(0, reader.getLineNumber());
        Assert.assertEquals(4, reader.getCol());
    }
    
    
    
    @Test
    public void testReader16() throws IOException {
        final String input = "abcd\ncd";
        final PushbackReader reader = this.obtain(input);
        Assert.assertEquals('a', reader.read());
        Assert.assertEquals('b', reader.read());
        Assert.assertEquals('c', reader.read());
        Assert.assertEquals('d', reader.read());
        reader.pushback('x');
        Assert.assertEquals('x', reader.read());
        Assert.assertEquals('\n', reader.read());
        Assert.assertEquals(1, reader.getLineNumber());
        Assert.assertEquals(0, reader.getCol());
        
        reader.pushback('\n');
        Assert.assertEquals(0, reader.getLineNumber());
        Assert.assertEquals(4, reader.getCol());
    }
}
