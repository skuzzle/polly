package de.skuzzle.polly.core.parser;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * A {@link Reader} implementation which keeps track of current line number and the 
 * position within the current line. It also remembers the length of every completely 
 * read line.
 * 
 * @author Simon Taddiken
 */
public class PositioningReader extends LineNumberReader {

    private int col;
    private int markedCol;
    private int[] colLengths;
    
    
    
    public PositioningReader(Reader in) {
        super(in);
        this.colLengths = new int[256];
    }

    
    
    /**
     * Gets the index within current line. The beginning of a line as index 0.
     * @return 0 based column index within current line.
     */
    public int getCol() {
        return this.col;
    }
    
    
    
    /**
     * Sets the current column position to the given value.
     * @param col The new column position.
     */
    protected void setCol(int col) {
        this.col = col;
    }
    
    
    
    /**
     * Gets the length in characters of the given line. The new line character is not
     * counted. Thus, in the String <code>"\n"</code>, line
     * 0 would have length 0. Note that line numbers can only be retrieved for lines that 
     * have been completely read so far. Those are all positive numbers (and 0) 
     * &lt; {@link #getLineNumber()}.
     * 
     * @param lineNumber 0 based line index.
     * @return Number of characters in that line.
     */
    public int getCols(int lineNumber) {
        if (lineNumber < 0 || lineNumber > this.getLineNumber()) {
            throw new IllegalArgumentException("invalid line number: " + lineNumber); //$NON-NLS-1$
        }
        return this.colLengths[lineNumber];
    }
    
    
    
    
    private void ensureIndexExists(int idx) {
        if (this.colLengths.length < idx + 1) {
            this.colLengths = Arrays.copyOf(this.colLengths, 
                Math.max(idx, this.colLengths.length + 512));
        }
    }
    
    
    
    @Override
    public int read() throws IOException {
        int lineBefore = this.getLineNumber();
        final int c = super.read();
        if (c == -1) {
            this.colLengths[this.getLineNumber()] = this.col;
        } else if (lineBefore != this.getLineNumber()) {
            this.ensureIndexExists(lineBefore);
            this.colLengths[lineBefore] = this.col;
            this.col = 0;
        } else {
            ++this.col;
        }
        return c;
    }

    
    
    @Override
    public void mark(int readAheadLimit) throws IOException {
        synchronized (lock) {
            super.mark(readAheadLimit);
            this.markedCol = this.col;
        }
    }
    
    
    
    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            super.reset();
            this.col = this.markedCol;
        }
    }
}
