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
 * onto the stream so it will be read next. You may specify a {@link PushbackStrategy}
 * to change the behavior of the pushback method. The default strategy is LIFO (this
 * takes only effect when pushing back more than one character).</p>
 * 
 * 
 * 
 * <p>Until now, this class only works for input strings encoded 
 * in ISO-8859-1.</p>
 * 
 * @author Simon
 */
public abstract class AbstractTokenStream implements Iterable<Token> {
    
    public static abstract class PushbackStrategy<T> {
        public abstract T pop(LinkedList<T> list);
        public abstract void push(LinkedList<T> list, T value);
        public abstract T peek(LinkedList<T> list);
    }
    
    
    
    public final static PushbackStrategy<Integer> CHARACTER_LIFO = 
        new PushbackStrategy<Integer>() {
        @Override
        public void push(LinkedList<Integer> list, Integer value) {
            list.push(value);
        }
        @Override
        public Integer pop(LinkedList<Integer> list) {
            return list.pop();
        }
        @Override
        public Integer peek(LinkedList<Integer> list) {
            return list.peekFirst();
        }
    };
    
    
    
    public final static PushbackStrategy<Integer> CHARACTER_FIFO = 
        new PushbackStrategy<Integer>() {
        @Override
        public void push(LinkedList<Integer> list, Integer value) {
            list.add(value);
        }
        @Override
        public Integer pop(LinkedList<Integer> list) {
            return list.remove();
        }
        @Override
        public Integer peek(LinkedList<Integer> list) {
            return list.peekFirst();
        }
    };
    
    
    
    public final static PushbackStrategy<Token> TOKEN_LIFO = 
        new PushbackStrategy<Token>() {
        @Override
        public void push(LinkedList<Token> list, Token value) {
            list.push(value);
        }
        @Override
        public Token pop(LinkedList<Token> list) {
            return list.pop();
        }
        @Override
        public Token peek(LinkedList<Token> list) {
            return list.peekFirst();
        }
    };
    
    
    
    public final static PushbackStrategy<Token> TOKEN_FIFO = 
        new PushbackStrategy<Token>() {
        @Override
        public void push(LinkedList<Token> list, Token value) {
            list.add(value);
        }
        @Override
        public Token pop(LinkedList<Token> list) {
            return list.remove();
        }
        @Override
        public Token peek(LinkedList<Token> list) {
            return list.peekFirst();
        }
    };
    
    
    
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
     * The strategy for pushing back characters.
     */
    protected PushbackStrategy<Integer> characterStrategy;
    
    /**
     * The strategy for pushing back tokens.
     */
    protected PushbackStrategy<Token> tokenStrategy;
    
    /**
     * Stringbuilder holds the current lexem. Will be resettet upon calling 
     * {@link #getLexem()}
     */
    protected StringBuilder currentLexem;
    
    
    
    /**
     * Creates a new TokenStream with the given String as input. 
     * It assumes the String to be ISO-8859-1 encoded!
     * @param stream The String to scan for tokens.
     * @throws UnsupportedEncodingException If the given encoding string is not supported.
     */
    public AbstractTokenStream(String stream) throws UnsupportedEncodingException {      
        this(stream, "ISO-8859-1");
    }
    
    
    
    /**
     * Creates a new TokenStream with the given String as input. 
     * 
     * @param stream The String to scan for tokens.
     * @param charset The name of the charset in which the stream is encoded.
     * @throws UnsupportedEncodingException If the given encoding string is not supported.
     */
    public AbstractTokenStream(String stream, String charset) 
        throws UnsupportedEncodingException {      
        InputStream inp = new ByteArrayInputStream(
                stream.getBytes(Charset.forName(charset)));

        this.reader = new BufferedReader(new InputStreamReader(inp, charset));
        this.pushbackBuffer = new LinkedList<Integer>();
        this.tokenBuffer = new LinkedList<Token>();
        this.characterStrategy = AbstractTokenStream.CHARACTER_FIFO;
        this.tokenStrategy = AbstractTokenStream.TOKEN_LIFO;
        this.currentLexem = new StringBuilder();
    }    
    
    
    
    /**
     * Creates a new TokenStream with the given {@link InputStream} 
     * as input.
     * 
     * @param stream The InputStream to scan for tokens.
     * @param charset The name of the charset in which the characters from the stream are
     *      encoded.
     * @throws UnsupportedEncodingException If the provided charset is not supported.
     */
    public AbstractTokenStream(InputStream stream, String charset) 
            throws UnsupportedEncodingException {
        this.reader = new InputStreamReader(stream, charset);
        this.pushbackBuffer = new LinkedList<Integer>();
        this.tokenBuffer = new LinkedList<Token>();
        this.characterStrategy = AbstractTokenStream.CHARACTER_FIFO;
        this.tokenStrategy = AbstractTokenStream.TOKEN_FIFO;
        this.currentLexem = new StringBuilder();
    }
    
    
    
    /**
     * Sets the strategy for pushing back characters. Strategy may be hotswapped - 
     * that is replacing the strategy during reading characters. 
     * 
     * @param strategy The strategy to set.
     */
    public void setCharacterStrategy(PushbackStrategy<Integer> strategy) {
        this.characterStrategy = strategy;
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
        /*if (this.lookahead == null) {
            this.lookahead = this.nextToken();
        }
        return this.lookahead;*/
        if (this.tokenBuffer.isEmpty()) {
            this.tokenStrategy.push(this.tokenBuffer, this.readToken());
        }
        return this.tokenStrategy.peek(this.tokenBuffer);
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
        return new Position(start, this.getStreamIndex() - 1);
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
        return new Position(token.getPosition().getStart(), this.getStreamIndex() - 1);
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
        this.characterStrategy.push(this.pushbackBuffer, t);
        this.eos = false;
        --this.streamIndex;
    }
    
    
    
    /**
     * Pushes back one token. The pushedback token will be buffered and read by later
     * calls of {@link #readToken()}. Depending what pushback strategy you set, the
     * token may be read by the next call to {@link #readToken()} or at later time.
     * 
     * @param t The token to push back.
     */
    public void pushback(Token t) {
        this.tokenStrategy.push(this.tokenBuffer, t);
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
        int next;
        boolean popped = false;
        
        if (!this.pushbackBuffer.isEmpty()) {
            next = this.characterStrategy.pop(this.pushbackBuffer);
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
            return this.tokenStrategy.pop(this.tokenBuffer);
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
     * Determines if a character is valid to start an identifier. That is if
     * its a '_' or it lies between 'A' and 'Z' or 'a' and 'z'.
     * 
     * @param token The character to check.
     * @return {@code true} if the given character is valid for an identifier.
     * @deprecated Use Character class method.
     */
    protected boolean canStartIdentifier(char token) {
        return (token >= 'A' && token <= 'Z') 
                || (token >= 'a' && token <= 'z')
                || (token == '_');
    }
    
    
    
    /**
     * Determines if a character is valid for an identifier. That is if
     * its a '_' or it lies between 'A' and 'Z' or 'a' and 'z' or is a number.
     * 
     * @param token The character to check.
     * @return {@code true} if the given character is valid for an identifier.
     * @deprecated Use Character class method.
     */
    protected boolean isIdentifierChar(char token) {
        return this.canStartIdentifier(token) || this.canStartNumber(token);
    }
    
    
    
    /**
     * Determines if a character is a valid number. That is if it is either of the 
     * following: {@code '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'}.
     * @param token The character to check.
     * @return {@code true} if the given character is a valid number.
     * @deprecated Use Character class method.
     */
    protected boolean canStartNumber(char token) {
        return token >= '0' && token <= '9';
    }
    

    
    /**
     * Checks whether given character is a whitespace character. This is equivalent to
     * {@code Character.isWhitespace(c)}
     * 
     * @param c The character to check.
     * @return {@code true} if c is a whitespace character.
     * @deprecated Use Character class method.
     */
    protected boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c);
    }
    
    
    
    /**
     * Converts a digit character to its corresponding integer value.
     * That is exactly {@code token - '0'}.
     *  
     * @param token The character to convert.
     * @return The integer representing the characters value.
     * @throws IllegalArgumentException If the input character is no digit.
     * @deprecated Use Character class method.
     */
    protected int toNumber(int token) {
        if (!Character.isDigit(token)) {
            throw new IllegalArgumentException(
                    "Input must be a character between '0' and '9'");
        }
        return token - '0';
    }
    

    
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
                   !AbstractTokenStream.this.tokenBuffer.isEmpty();
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