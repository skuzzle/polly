package de.skuzzle.polly.core.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import de.skuzzle.polly.tools.Immutable;

/**
 * Represents a span within a one-lined String. It consists of a inclusive 
 * start position and an exclusive end position.
 * 
 * For convenience, a Position has methods to retrieve substrings of a given String:
 * {@link #prefix(String)} which returns a String which consists of all characters 
 * before this Position.
 * {@link #substring(String)} which returns exactly the String which Position this
 * object represents and {@link #postfix(String)} which returns a String consisting of
 * all characters which follow this span.   
 * 
 * @author Simon
 */
public final class Position implements Equatable, Immutable, 
        Comparable<Position> {
    
    
    /**
     * Creates a position that spans from the lower of the given positions to the greater 
     * of the given positions.
     * 
     * @param pos Array of positions.
     * @return A new position that spans from the start of the lowest position to the end 
     *          of the greatest position. 
     */
    public final static Position correctSpan(Position...pos) {
        if (pos.length == 0) {
            throw new IllegalArgumentException("zero length array"); //$NON-NLS-1$
        }
        Arrays.sort(pos);
        if (pos[0] == Position.NONE || pos[pos.length - 1] == Position.NONE) {
            return Position.NONE;
        }
        return new Position(pos[0], pos[pos.length - 1]);
    }
    

    
    /**
     * A Position instance that represents no valid position within a String. Can be used
     * for internally created AST Nodes, that have no matching representation within
     * the input String.
     */
    public final static Position NONE = new Position(-1, 0);
    
    
    
    /**
     * Creates a list of indicator strings from the given collection of positions. 
     * Disjunct position will be placed in one line.
     * 
     * @param positions Collection of positions.
     * @param offset Offset to add to each position before creating the string.
     * @return A list of error indicator strings.
     */
    public static List<String> indicatorStrings(Collection<Position> positions, 
            int offset) {
        
        final List<String> result = new ArrayList<String>(positions.size());
        final TreeSet<Position> posis = new TreeSet<Position>(positions);
        
        while (!posis.isEmpty()) {
            final Position next = posis.pollFirst();
            final StringBuilder b = new StringBuilder();
            b.append(next.offset(offset).errorIndicatorString());
            
            final Iterator<Position> it = posis.iterator();
            while (it.hasNext()) {
                final Position pos = it.next();
                if (next.overlap(pos)) {
                    break;
                } else {
                    final Position off = pos.offset(offset);
                    while (b.length() < off.start) {
                        b.append(" "); //$NON-NLS-1$
                    }
                    b.append("^"); //$NON-NLS-1$
                    if (off.getWidth() > 1) {
                        while (b.length() < off.end - 1) {
                            b.append("-"); //$NON-NLS-1$
                        }
                        b.append("^"); //$NON-NLS-1$
                    }
                    it.remove();
                }
            }
            result.add(b.toString());
        }
        
        return result;
    }

    
    
    private final int start;
    private final int end;
    
    
    
    /**
     * Creates a new Position Object with given start and end index.
     * 
     * @param start The inclusive start index of this Position.
     * @param end The exclusive end index of this position.
     */
    public Position(int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException(
                "start >= end (" + start + ">=" + end + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        this.start = start;
        this.end = end;
    }
    
    
    
    /**
     * Creates a new Position using the positions of the given tokens. The new
     * position will span from the start of the left token until the end of the 
     * right token.
     * 
     * @param left The left token.
     * @param right The right token.
     */
    public Position(Token left, Token right) {
        this(left.getPosition(), right.getPosition());
    }
    
    
    
    /**
     * Creates a new Position using the start and end indizes of the given positions.
     * The new Position will span from the start of the left position until the end of
     * the right position.
     * 
     * @param left The left position.
     * @param right The right position.
     */
    public Position(Position left, Position right) {
        this(left.getStart(), right.getEnd());
    }
    
    
    
    /**
     * Creates a new position by clipping this position to the given position if 
     * necessary. That means the resulting position starts at 
     * <code>max(this.start, other.start)</code> and ends at 
     * <code>min(this.end, other.end)</code>.
     * 
     * @param other Position to clip this to.
     * @return A new clipped position.
     */
    public Position clip(Position other) {
        return this.clip(other.start, other.end);
    }
    
    
    
    /**
     * Creates a new position by clipping this position to the given start and end value
     * if necessary. That means the resulting position starts at 
     * <code>max(this.start, start)</code> and ends at 
     * <code>min(this.end, end)</code>.
     * 
     * @param start Minimum starting position.
     * @param end  Maximum ending position.
     * @return A new clipped position.
     */
    public Position clip(int start, int end) {
        start = Math.max(start, this.start);
        end = Math.min(end, this.end);
        if (start == end) {
            ++end;
        }
        return new Position(start, end);
    }
    
    
    
    /**
     * Determines whether this position and the given represent overlapping string parts.
     * 
     * @param position Position to compare.
     * @return If the positions overlap each other.
     */
    public boolean overlap(Position position) {
        if (this.start < position.start) {
            return this.end > position.start;
        } else {
            return position.end > this.start;
        }
    }
    
    
    
    /**
     * Returns whether this position is completely inside the other position.
     * 
     * @param other The other position.
     * @return Whether this position is inside the other one.
     */
    public boolean isInside(Position other) {
        return this.start >= other.start && this.end <= other.end;
    }
    
    
    
    /**
     * Creates a new Position by adding the given offset to this position's start and 
     * end.
     * 
     * @param offset The offset to end.
     * @return A new position.
     */
    public Position offset(int offset) {
        return new Position(this.start + offset, this.end + offset);
    }
    
    
    
    /**
     * Creates a new position spanning from the start of the given position to the end
     * of this position.
     * 
     * @param start Start position.
     * @return A new position.
     */
    public Position spanFrom(Position start) {
        return new Position(start, this);
    }
    
    
    
    /**
     * Creates a new position spanning from the start of this position to the end of the
     * given position.
     * 
     * @param end End position.
     * @return A new position.
     */
    public Position spanTo(Position end) {
        return new Position(this, end);
    }
    
    
    
    /**
     * Returns the starting index of this Position.
     * @return The inclusive start index.
     */
    public int getStart() {
        return this.start;
    }
    
    
    
    /**
     * Returns the end index of this Position.
     * @return The inclusive end index.
     */
    public int getEnd() {
        return this.end;
    }
    
    
    
    /**
     * Gets the count of characters that this position spans.
     * 
     * @return Character count between end and start position.
     */
    public int getWidth() {
        return this.end - this.start;
    }
    
    
    
    /**
     * Gives a String which indicates this position. That means, the returned String will
     * consist of spaces until the start position. There, a "^" will be printed to 
     * indicate the beginning of this position. If {@link #getWidth()} is greater than
     * 1, a second "^" will be printed at the end position. So the returned String will
     * have the length <code>getWidth() > 1 ? this.getEnd() : this.getStart()</code>
     * 
     * @return A String indicating this position.
     */
    public String errorIndicatorString() {
        if (this == Position.NONE) {
            return ""; //$NON-NLS-1$
        }
        final StringBuilder b = new StringBuilder(this.end);
        int i = 0;
        for (; i < this.start; ++i) {
            b.append(" "); //$NON-NLS-1$
        }
        b.append("^"); //$NON-NLS-1$
        if (this.getWidth() > 1) {
            for (; i < this.end - 2; ++i) {
                b.append("-"); //$NON-NLS-1$
            }
            b.append("^"); //$NON-NLS-1$
        }
        return b.toString();
    }
    
    
    
    /**
     * Returns a prefix of the given String consisting of all characters with index
     * lower than {@link #getStart()}.
     * 
     * @param original The String in which this object represents a Position.
     * @return A substring of the original String.
     * @throws IllegalArgumentException If the original String is too short.
     */
    public String prefix(String original) {
        if (this.equals(Position.NONE)) {
            return original;
        } else if (this.start > original.length()) {
            throw new IllegalArgumentException("Original String is too short!"); //$NON-NLS-1$
        }
        return original.substring(0, this.start);
    }
    
    
    
    /**
     * Returns the postfix of the given string which consists of all characters that
     * occur after this positions end index (including the character at the end index
     * itself, because it is exclusive).
     * 
     * @param original The string to create the postfix from.
     * @return A postfix of that string.
     */
    public String postfix(String original) {
        if (this.equals(Position.NONE)) {
            return original;
        } else if (this.start == this.end - 1 && this.start == original.length()) {
            return " "; //$NON-NLS-1$
        }
        return original.substring(this.end);
    }
    
    
    
    /**
     * Returns the substring of the given string that this position represents. If this
     * is {@link Position#NONE}, the whole other string will be returned.
     * 
     * @param original The string to create the substring from.
     * @return The substring.
     */
    public String substring(String original) {
        if (this.equals(Position.NONE)) {
            return original;
        } else if (this.start == this.end - 1 && this.start == original.length()) {
            return " "; //$NON-NLS-1$
        }
        return original.substring(this.start, this.end);
    }
    
    
    
    public String mark(String original) {
        return this.mark(original, "4\u001F", "\u001F"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    
    public String mark(String original, String begin, String end) {
        StringBuilder result = new StringBuilder(original.length() + 19);

        result.append(this.prefix(original));
        result.append(begin);
        result.append(this.substring(original));
        result.append(end);
        result.append(this.postfix(original));
        return result.toString();
    }
    
    
    
    @Override
    public String toString() {
        return (this.start + 1) + (this.getWidth() == 1 ? "" : "-" + (this.end + 1)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.end;
        result = prime * result + this.start;
        return result;
    }

    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Position.class;
    }
    


    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final Position other = (Position) o;
        return this.start == other.start && this.end == other.end;
    }



    @Override
    public int compareTo(Position o) {
        int r = this.start - o.start;
        if (r == 0) {
            r = this.end - o.end;
        }
        return r;
    }
}
