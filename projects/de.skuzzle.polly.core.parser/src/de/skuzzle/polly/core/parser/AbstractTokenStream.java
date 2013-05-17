package de.skuzzle.polly.core.parser;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;



/**
 * <p>This is an abstract base class for all classes that are 
 * to provide String-scanning for tokens. Extending classes only 
 * need to implement the {@link #readToken()}  method in order to 
 * provide their own token forming rules.</p>
 * 
 * <p>This class then provides many convenience functions for 
 * context-free string parsing such as looking one token ahead, 
 * consuming tokens, match the next token against an expected 
 * token or skipping tokens over.</p>
 * 
 * <p>This class does position tracking within the input which works 
 * best for one lined input strings as it does not track line 
 * breaks.</p>
 * 
 * <p>To implement {@link #readToken()} this class provides the 
 * methods {@link #readChar()} which reads exactly one char from 
 * the input and {@link #pushBack(int)} which can put a char back 
 * onto the stream so it will be read next.</p>
 * 
 * @author Simon
 */
public abstract class AbstractTokenStream implements Iterable<Token>, TokenStream {
    
    
    /**
     * Stream which is used to read chars from the input.
     */    
    protected PushbackReader reader;
    
    /**
     * The pushback buffer for characters
     */
    protected LinkedList<Integer> pushbackBuffer;
    
    /**
     * Pushbackbuffer for tokens.
     */
    protected LinkedList<Token> tokenBuffer;
    
    /** Holds all tokens in order they have been consumed. */
    protected final List<Token> consumedTokens;
    
    /** Pointer which points to the last consumed token in {@link #consumedTokens}. */
    protected int tokenIndex;

    /** Pointer for marking a position in {@link #consumedTokens}. */
    protected int mark;
    
    
    
    /**
     * Creates a new TokenStream with the given String as input. It will use the
     * systems default charset.
     * 
     * @param stream The String to scan for tokens.
     */
    public AbstractTokenStream(String stream) {      
        this(stream, Charset.defaultCharset());
    }
    
    
    
    /**
     * Creates a new TokenStream with the given String as input. 
     * 
     * @param stream The String to scan for tokens.
     * @param charset The charset in which the stream is encoded.
     */
    public AbstractTokenStream(String stream, Charset charset) {      
        InputStream inp = new ByteArrayInputStream(stream.getBytes(charset));
        this.reader = new PushbackReader(new BufferedReader(
            new InputStreamReader(inp, charset)));
        this.pushbackBuffer = new LinkedList<Integer>();
        this.tokenBuffer = new LinkedList<Token>();
        this.consumedTokens = new ArrayList<Token>();
        this.mark = -1;
    }    
    
    
    
    /**
     * Creates a new TokenStream with the given {@link InputStream} 
     * as input.
     * 
     * @param stream The InputStream to scan for tokens.
     * @param charset The name of the charset in which the characters from the stream are
     *      encoded.
     */
    public AbstractTokenStream(InputStream stream, Charset charset) {
        this.reader = new PushbackReader(new InputStreamReader(stream, charset));
        this.pushbackBuffer = new LinkedList<Integer>();
        this.tokenBuffer = new LinkedList<Token>();
        this.consumedTokens = new ArrayList<Token>();
        this.mark = -1;
    }
    
    
    
    /**
     * Whether all characters have been read from the input.
     * 
     * @return Whether all characters have been read from the input.
     */
    public boolean eos() {
        return this.reader.eos();
    }
    
    
    
    /**
     * Gets a {@link TokenStream} view of all consumed tokens. The last token returned
     * by this stream will always have the type {@link TokenType#EOS}.
     * 
     * @return A TokenStream that can read all tokens that already have been consumed.
     */
    public TokenStream consumed() {
        return new TokenStream() {
            
            private int i = 0;
            
            @Override
            public boolean match(Token token) throws ParseException {
                return this.match(token.getType());
            }
            
            
            
            @Override
            public boolean match(TokenType type) throws ParseException {
                final TokenType compare = this.indexExists(this.i + 1) 
                    ? TokenType.EOS : 
                        consumedTokens.get(this.i + 1).getType();
                if (compare == type) {
                    ++this.i;
                    return true;
                }
                return false;
            }
            
            
            
            @Override
            public Token lookAhead() throws ParseException {
                if (!this.indexExists(this.i + 1)) {
                    return new Token(TokenType.EOS, Position.NONE);
                }
                return consumedTokens.get(this.i + 1);
            }
            
            
            
            @Override
            public Token consume() throws ParseException {
                return consumedTokens.get(this.i++);
            }
            
            
            
            private boolean indexExists(int index) {
                return index < consumedTokens.size();
            }
        };
    }
    
    
    
    /* (non-Javadoc)
     * @see de.skuzzle.polly.parsing.TokenStream#match(de.skuzzle.polly.parsing.TokenType)
     */
    @Override
    public boolean match(TokenType type) throws ParseException {
        if (this.lookAhead().matches(type)) {
            this.consume();
            return true;
        }
        return false;
    }
    
    
    
    /* (non-Javadoc)
     * @see de.skuzzle.polly.parsing.TokenStream#match(de.skuzzle.polly.parsing.Token)
     */
    @Override
    public boolean match(Token token) throws ParseException {
        return this.match(token.getType());
    }

    
    
    /* (non-Javadoc)
     * @see de.skuzzle.polly.parsing.TokenStream#lookAhead()
     */
    @Override
    public Token lookAhead() throws ParseException {
        if (this.tokenBuffer.isEmpty()) {
            this.tokenBuffer.add(this.nextToken());
        }
        return this.tokenBuffer.peek();
    }
    
    
    
    /**
     * Consumes tokens until the next token to be consumed has 
     * any of the given types or the end of the stream has been reached.
     * 
     * @param types Array of {@link TokenType}s to skip until.
     * @return The Token to which this method has been skipped. 
     *      That means that: 
     *      {@code Token la = synchronize(...) => la = this.lookAhead()}.
     * @throws ParseException If an invalid token has been read while skipping.
     */
    public Token synchronize(TokenType...types) throws ParseException {
        return this.synchronize(Arrays.asList(types));
    }
    
    
    
    /**
     * Consumes tokens until the next token to be consumed has 
     * any of the given types or the end of the stream has been reached.
     * 
     * @param types Array of {@link TokenType}s to skip until.
     * @return The Token to which this method has been skipped. 
     *      That means that: 
     *      {@code Token la = synchronize(...) => la = this.lookAhead()}.
     * @throws ParseException If an invalid token has been read while skipping.
     */
    public Token synchronize(Collection<TokenType> types) throws ParseException {
        while (!this.eos()) {
            final Token la = this.lookAhead();
            if (types.contains(la.getType())) {
                return la;
            }
            this.consume();
        }
        return this.lookAhead();
    }
    
    
    
    /* (non-Javadoc)
     * @see de.skuzzle.polly.parsing.TokenStream#consume()
     */
    @Override
    public Token consume() throws ParseException {
        return this.nextToken();
    }
    
    
    
    /**
     * Returns the current position within the input stream.
     * @return The current stream position.
     */
    public int getStreamIndex() {
        return this.reader.getPosition();
    }
    
    
    
    /**
     * Creates a new {@link Position} which spans from the given 
     * start index until the current stream index.
     * 
     * @param start The start index of the new {@link Position} object.
     * @return A new {@link Position} which represents the span from 
     *      {@code start} until {@link #getStreamIndex()}. 
     */
    public Position spanFrom(int start) {
        int endIdx = this.eos() ? this.getStreamIndex() + 1 : this.getStreamIndex();
        return new Position(start, endIdx);
    }
    
    
    
    /**
     * <p>
     * Creates a new {@link Position} which spans from the beginning 
     * of the given {@link Token} until the current stream index.
     * </p>
     * 
     * @param token The {@link Token} which states the beginning of the new 
     *              {@link Position} object.
     * @return A new {@link Position} which represents the span from 
     *        {@code token.getPosition.getStart()} until {@link #getStreamIndex()}. 
     */
    public Position spanFrom(Token token) {
        return this.spanFrom(token.getPosition());
    }
    
    
    
    /**
     * Creates a new {@link Position} which spans from the beginning of the given 
     * Position until the current stream index.
     * 
     * @param start Start position.
     * @return A new Position representing the span from start until 
     *          {@link #getStreamIndex()}
     */
    public Position spanFrom(Position start) {
        int endIdx = this.eos() ? this.getStreamIndex() + 1 : this.getStreamIndex();
        return new Position(start.getStart(), endIdx);
    }
    
    
    
    /**
     * <p>
     * Pushes one character back onto the input and decreases the streampointer by 1.
     * </p>
     * 
     * <p>This method will always set {@link #eos} to {@code false}.</p>
     * @param t The character to be pushed back onto the input.
     */
    protected void pushBack(int t) {
        this.reader.pushback(t);
    }
    
    
    
    protected void pushBackArtificial(int c) {
        this.reader.pushbackInvisible(c);
    }
    
    
    
    /**
     * <p>Pushes back one token. The pushed back token will be buffered and read by later
     * calls of {@link #readToken()}. The pushed back token will be appended to the head
     * of the token pushback buffer. That means the next call to {@link #readToken()} will
     * return the pushed back token.</p>
     * 
     * <p>Pushed back tokens are, when consumed, never added to the list of consumed 
     * tokens.</p> 
     * 
     * @param t The token to push back.
     */
    public void pushBackFirst(Token t) {
        this.tokenBuffer.addFirst(t);
    }
    
    
    
    /**
     * <p>Pushes back one token. The pushed back token will be buffered and read by later 
     * calls of {@link #readToken()}. The pushed back token will be appended to the tail
     * of the token pushback buffer.</p>
     * 
     * <p>Pushed back tokens are, when consumed, never added to the list of consumed 
     * tokens.</p>
     * 
     * @param t Token to push back.
     */
    public void pushBackLast(Token t) {
        this.tokenBuffer.addLast(t);
    }
    
    
    
    /**
     * Consumes the next character only if it is the expected one.
     * 
     * @param c The expected character.
     * @return Whether the next character is the expected one.
     */
    protected boolean nextIs(int c) {
        final int next = this.readChar();
        if (next == c) {
            return true;
        }
        this.pushBack(next);
        return false;
    }

    
    
    /**
     * <p>
     * Reads the next character from the input and increases the streampointer by 1.
     * If the end of the input is reached, this method sets the attribute {@link #eos} 
     * to {@code true}.
     * </p>
     * 
     * @return The character that has been read from the input or {@code -1} if the end
     *         of the input has been reached.
     */
    protected int readChar() {
        try {
            return this.reader.read();
        } catch (IOException e) {
            return -1;
        }
    }
    
    
    
    /**
     * <p>Remembers the current position within the token stream. The token stream can
     * be reset to this mark by using {@link #reset(boolean, boolean)}. Doing so will 
     * cause the token to where the stream has been reseted to be the next token returned 
     * by <code>nextToken()</code> or any other methods that would return a token.</p>
     */
    public void mark() {
        this.mark = this.tokenIndex;
    }
    
    
    
    /**
     * Resets this stream to the mark set by {@link #mark()}. If the token buffer is 
     * empty, the token that the stream was reset to will be the next one to be read.
     * If the token buffer contains tokens, they will be read first until it is empty.
     * 
     * @param removeMark Whether the mark should be removed.
     * @param clearBuffer Whether the token buffer should be cleared.
     */
    public void reset(boolean removeMark, boolean clearBuffer) {
        if (this.mark == -1) {
            throw new IllegalStateException("no mark available");
        }
        this.tokenIndex = this.mark;
        if (removeMark) {
            this.mark = -1;
        }
        if (clearBuffer) {
            this.tokenBuffer.clear();
        }
    }

    
    
    /**
     * Reads the next token from the input and consumes it. If the token has already 
     * been read (due to call of {@link #lookAhead()}, the token will not be read again.
     * In that case, the token will be retrieved from the lookahead buffer and then be
     * returned.
     * @return The next token in the input stream.
     * @throws ParseException If no valid token could be read.
     */
    private Token nextToken() throws ParseException {
        if (!this.tokenBuffer.isEmpty()) {
            return this.tokenBuffer.poll();
        }
        final Token next;
        if (this.tokenIndex < this.consumedTokens.size()) {
            next = this.consumedTokens.get(this.tokenIndex);
        } else {
            next = this.readToken();
            this.consumedTokens.add(next);
        }
        ++this.tokenIndex;
        return next;
    }
    
    
    
    /**
     * Main method for reading tokens from the input stream.
     * 
     * @return The next token in the input stream.
     * @throws ParseException If no valid token could be read.
     */
    protected abstract Token readToken() throws ParseException;
    


    private Iterator<Token> tokenIterator;
    
    @Override
    public synchronized Iterator<Token> iterator() {
        if (this.tokenIterator == null) {
            this.tokenIterator = new TokenIterator();
        }
        return this.tokenIterator;
    }
    
    
    
    private class TokenIterator implements Iterator<Token> {

        @Override
        public boolean hasNext() {
            return !AbstractTokenStream.this.eos() && 
                   AbstractTokenStream.this.tokenBuffer.isEmpty();
        }
        
        
        
        @Override
        public Token next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return AbstractTokenStream.this.nextToken();
            } catch (ParseException e) {
                throw new RuntimeException("ParseException occurred", e);
            }
        }



        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}