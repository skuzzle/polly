package de.skuzzle.polly.parsing;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;



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
public abstract class AbstractTokenStream implements Iterable<Token> {
    
    
    /**
     * Stream which is used to read chars from the input.
     */    
    protected Reader reader;
    
    /**
     * The pushback buffer for characters
     */
    protected LinkedList<Integer> pushbackBuffer;
    
    /**
     * Pushbackbuffer for tokens.
     */
    protected LinkedList<Token> tokenBuffer;
    
    /**
     * States whether the end of the input has been reached.
     */
    protected boolean eos;
    
    /**
     * Lookahead buffer.
     */
    protected Token lookahead;
    
    /**
     * The current stream position.
     */
    protected int streamIndex;
    
    /**
     * Stringbuilder holds the current lexem. Will be reseted upon calling 
     * {@link #getLexem()}
     */
    protected StringBuilder currentLexem;
    
    
    
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
        this.reader = new BufferedReader(new InputStreamReader(inp, charset));
        this.pushbackBuffer = new LinkedList<Integer>();
        this.tokenBuffer = new LinkedList<Token>();
        this.currentLexem = new StringBuilder();
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
        this.reader = new InputStreamReader(stream, charset);
        this.pushbackBuffer = new LinkedList<Integer>();
        this.tokenBuffer = new LinkedList<Token>();
        this.currentLexem = new StringBuilder();
    }
    
    
    
    /**
     * Consumes the next token only if it has the expected type.
     * 
     * @param type The {@link TokenType} you expect the next token to be.
     * @return {@code true} if the consumed token has the expected type.
     * @throws ParseException If no valid token could be read.
     */
    public boolean match(TokenType type) throws ParseException {
        if (this.lookAhead().matches(type)) {
            this.consume();
            return true;
        }
        return false;
    }
    
    
    
    /**
     * Consumes the next token and checks whether it has the same type as the given
     * token.
     * 
     * @param token The token (in most cases this is the current lookahead token).
     * @return {@code true} if the consumed token has the expected type.
     * @throws ParseException If no valid token could be read.
     */
    public boolean match(Token token) throws ParseException {
        return this.match(token.getType());
    }

    
    
    /**
     * Returns the next token without consuming it. That means:
     * 
     * {@code Token la = lookAhead() => match(la.getType()) == true}. 
     * @return The next token which will be consumed calling 
     *      {@link #match(TokenType)} or {@link #consume()}. 
     * @throws ParseException If no valid token could be read.
     */
    public Token lookAhead() throws ParseException {
        if (this.tokenBuffer.isEmpty()) {
            this.tokenBuffer.add(this.readToken());
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
     *      {@code Token la = skipUntilNextIs(...) => la = this.lookAhead()}.
     * @throws ParseException If an invalid token has been read while skipping.
     */
    public Token skipUntilNextIs(TokenType...types) throws ParseException {
        while (!this.eos) {
            Token la = this.lookAhead();
            for (TokenType type : types) {
                if (la.matches(type)) {
                    return la;
                }
            }
            this.consume();
        }
        return this.lookAhead();
    }
    
    
    
    /**
     * Consumes the next token without returning it.
     * @throws ParseException If no valid token could be read.
     */
    public void consume() throws ParseException {
        this.nextToken();
    }
    
    
    
    /**
     * Returns the current position within the input stream.
     * @return The current stream position.
     */
    public int getStreamIndex() {
        return this.streamIndex;
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
        int endIdx = this.eos ? this.getStreamIndex() - 1 : this.getStreamIndex();
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
        int endIdx = this.eos ? this.getStreamIndex() - 1 : this.getStreamIndex();
        return new Position(start.getStart(), endIdx);
    }
    
    
    
    /**
     * <p>
     * Pushes one character back onto the input and decreases the streampointer by 1.
     * </p>
     * <p>
     * The concrete behavior of this method depends on the currently set Pushback 
     * strategy for characters. By default, pushing back and reading characters behaves
     * like a stack.
     * </p>
     * 
     * <p>This method will always set {@link #eos} to {@code false}.</p>
     * @param t The character to be pushed back onto the input.
     */
    protected void pushBack(int t) {
        this.pushbackBuffer.add(t);
        this.eos = false;
        --this.streamIndex;
    }
    
    
    
    /**
     * Pushes back one token. The pushed back token will be buffered and read by later
     * calls of {@link #readToken()}. The pushed back token will be appended to the head
     * of the token pushback buffer. That means the next call to {@link #readToken()} will
     * return the pushed back token.
     * 
     * @param t The token to push back.
     */
    public void pushBackFirst(Token t) {
        this.tokenBuffer.addFirst(t);
    }
    
    
    
    /**
     * Pushes back one token. The pushed back token will be buffered and read by later 
     * calls of {@link #readToken()}. The pushed back token will be appended to the tail
     * of the token pushback buffer.
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
     * @return The character that has been read from the input or {@code \0} if the end
     *         of the input has been reached.
     */
    protected int readChar() {
        if (this.eos) {
            throw new IllegalStateException("end of stream reached");
        }
        
        int next;
        boolean popped = false;
        
        if (!this.pushbackBuffer.isEmpty()) {
            next = this.pushbackBuffer.poll();
            popped = true;
        } else {
            try {
                next = this.reader.read();
            } catch (IOException e) {
                next = -1;
            }
        }
        
        if (next == -1 || next == '\0') {
            next = -1;
            this.eos = true;
        } else if (!popped) {
            this.currentLexem.appendCodePoint(next);
        }
        
        ++this.streamIndex;
        return next;
    }
    
    
    
    /**
     * Returns a String consisting of the characters that have been read since the last 
     * call of this method or the creation of this stream.
     * 
     * Pushed back characters will not(!) be added to this lexem twice. They are added
     * when reading them the first time.
     * 
     * @return The lexem string.
     */
    public String getLexem() {
        String tmp = this.currentLexem.toString();
        this.currentLexem = new StringBuilder();
        return tmp;
    }
    
    
    
    /**
     * Looks n characters ahead without consuming them.
     * @param n The amount of characters to look ahead.
     * @return The n'th character from the current stream position.
     * @deprecated This method has not been tested, is inefficient and not needed.
     */
    @Deprecated
    protected int readAhead(int n) {
        Queue<Integer> q = new LinkedList<Integer>();
        int la = 0;

        for (int i = 0; i < n; ++i) {
            la = this.readChar();
            q.offer(la);
        }
        
        while (!q.isEmpty()) {
            this.pushBack(q.poll());
        }
        
        return la;
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
        
        return this.readToken();
    }
    
    
    
    /**
     * Main method for reading tokens from the input stream.
     * 
     * @return The next token in the input stream.
     * @throws ParseException If no valid token could be read.
     */
    protected abstract Token readToken() throws ParseException;
    

    
    /**
     * Throws a {@link ParseException} with given message and position spanning from the
     * given stream index to the current stream index.
     * 
     * @param errorMessage The parse error message.
     * @param tokenStart The beginning of the errornous token.
     * @throws ParseException is always thrown.
     */
    protected void parseException(String errorMessage, int tokenStart) 
            throws ParseException {
        Position pos = this.spanFrom(tokenStart);
        throw new ParseException(errorMessage, pos);
    }
    
    
    
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
            return !AbstractTokenStream.this.eos && 
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